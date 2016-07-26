package org.nudge.elasticstack;

/**
 * @author Sarah Bourgeois
 * @author Frederic Massart
 * <p>
 * Description : Class which permits to send rawdatas to elasticSearch with -startDeamon
 */

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.nudge.apm.buffer.probe.RawDataProtocol.Layer;
import com.nudge.apm.buffer.probe.RawDataProtocol.RawData;
import com.nudge.apm.buffer.probe.RawDataProtocol.Transaction;
import org.apache.log4j.Logger;
import org.nudge.elasticstack.config.Configuration;
import org.nudge.elasticstack.connection.Connection;
import org.nudge.elasticstack.json.bean.EventTransaction;
import org.nudge.elasticstack.json.bean.MappingProperties;
import org.nudge.elasticstack.json.bean.MappingPropertiesBuilder;
import org.nudge.elasticstack.json.bean.NudgeEvent;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class Daemon {

    private static final Logger LOG = Logger.getLogger(Daemon.class);
    private static ScheduledExecutorService scheduler;
    private static final String lineBreak = "\n";
    private static List<String> analyzedFilenames = new ArrayList<>();
    private static final long ONE_MIN = 60000;

    /**
     * Description : Launcher Deamon.
     *
     * @param config
     *
     */
    public static void start(Configuration config) {
        scheduler = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
            public Thread newThread(Runnable runnable) {
                Thread thread = new Thread(runnable);
                thread.setName("nudge-es-daemon");
                thread.setDaemon(false);
                return thread;
            }
        });
        scheduleDaemon(scheduler, config);
    }

    private static void scheduleDaemon(ScheduledExecutorService scheduler, Configuration config) {
        scheduler.scheduleAtFixedRate(new DaemonTask(config), 0L, 1L, TimeUnit.MINUTES);
    }

    public static void stop() {
        scheduler.shutdown();
    }

    protected static class DaemonTask implements Runnable {
        private Configuration config;

        DaemonTask(Configuration config) {
            this.config = config;
        }

        /**
         * Description : Collect data from Nudge API and push it.
         */
        @Override
        public void run() {
            try {
                Connection c = new Connection(config.getNudgeUrl());
                c.login(config.getNudgeLogin(), config.getNudgePwd());
                for (String appId : config.getAppIds()) {
                    List<String> rawdataList = c.requestRawdataList(appId, "-10m");
                    // analyse files, comparaison and push
                    for (String rawdataFilename : rawdataList) {
                        if (!analyzedFilenames.contains(rawdataFilename)) {
                            RawData rawdata = c.requestRawdata(appId, rawdataFilename);
                            List<Transaction> transaction = rawdata.getTransactionsList();
                            List<EventTransaction> events = buildTransactionEvents(transaction);

                            for (EventTransaction eventTrans : events) {
                                nullLayer(eventTrans);
                            }
                            List<String> jsonEvents = parseJson(events);
                            pushMapping(config);
                            sendToElastic(jsonEvents);
                        }
                    }
                    analyzedFilenames = rawdataList;
                }
            } catch (Throwable t) {
                LOG.fatal("The daemon has encountered a crash error", t);
                if (null != scheduler) {
//                    Reschedule the daemonTask
                    LOG.info("Restart the daemon in " + ONE_MIN + "ms");
                    try {
                        Thread.sleep(ONE_MIN);
                    } catch (InterruptedException e) {
                        LOG.warn("Interrupted before a daemon restart", e);
                    }
                    LOG.info("Restarting daemon ...");
                    scheduleDaemon(scheduler, config);
                }
            }
        }


        private static void pushMapping(Configuration config) throws IOException {
            String elasticURL = config.getOutputElasticHosts();
            pushMapping(elasticURL, "nudge");

        }

        /**
         * Description : update default elasticsearch mapping
         *
         * @throws IOException
         */
        public static void pushMapping(String elasticURL, String index) throws IOException {
            ObjectMapper jsonSerializer = new ObjectMapper();
            MappingProperties mappingProperies = MappingPropertiesBuilder.buildMappingProperties("multi_field",
                    "string", "analyzed", "string", "not_analyzed");
            jsonSerializer.enable(SerializationFeature.INDENT_OUTPUT);
            String jsonEvent = jsonSerializer.writeValueAsString(mappingProperies);
            URL URL = new URL(elasticURL + index + "/transaction/_mapping");
            System.out.println("     ");
            System.out.println("      ");
            System.out.println(URL);

            HttpURLConnection httpCon2 = (HttpURLConnection) URL.openConnection();
            httpCon2.setDoOutput(true);
            httpCon2.setRequestMethod("PUT");
            OutputStreamWriter out = new OutputStreamWriter(httpCon2.getOutputStream());

            out.write(jsonEvent);
            out.close();
            LOG.info(" Transaction Mapping Flushed : " + httpCon2.getResponseCode() + " - "
                    + httpCon2.getResponseMessage());
        }

        /**
         * Description : recuperate datas from rawdatas and add it to parse.
         *
         * @param transactionList
         * @return
         * @throws ParseException
         * @throws JsonProcessingException
         */
        public List<EventTransaction> buildTransactionEvents(List<Transaction> transactionList)
                throws ParseException, JsonProcessingException {
            List<EventTransaction> events = new ArrayList<EventTransaction>();

            for (Transaction trans : transactionList) {
                String name = trans.getCode();
                SimpleDateFormat sdfr = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                String date = sdfr.format(trans.getStartTime());
                long response_time = trans.getEndTime() - trans.getStartTime();
                EventTransaction transactionEvent = new EventTransaction(name, response_time, date, 1L);
                events.add(transactionEvent);
                // handle layers
                buildLayerEvents(trans.getLayersList(), transactionEvent);
                events.add(transactionEvent);
            }
            return events;
        }

        /**
         * Description : build layer events
         *
         * @param rawdataLayers
         * @param eventTrans
         * @throws ParseException
         * @throws JsonProcessingException
         */
        public void buildLayerEvents(List<Layer> rawdataLayers, EventTransaction eventTrans)
                throws ParseException, JsonProcessingException {
            for (Layer layer : rawdataLayers) {
                if (layer.getLayerName().equals("SQL")) {
                    eventTrans.setResponseTimeLayerSql(layer.getTime());
                    eventTrans.setLayerCountSql(layer.getCount());
                    eventTrans.setLayerNameSql(layer.getLayerName());
                }
                if (layer.getLayerName().equals("JMS")) {
                    eventTrans.setResponseTimeLayerJms(layer.getTime());
                    eventTrans.setLayerCountJms(layer.getCount());
                    eventTrans.setLayerNameJms(layer.getLayerName());
                }
                if (layer.getLayerName().equals("JAX-WS")) {
                    eventTrans.setResponseTimeLayerJaxws(layer.getTime());
                    eventTrans.setLayerCountJaxws(layer.getCount());
                    eventTrans.setLayerNameJaxws(layer.getLayerName());

                }
                if (layer.getLayerName().equals("JAVA")) {
                    eventTrans.setLayerCountJava(layer.getCount());
                    eventTrans.setLayerNameJava(layer.getLayerName());
                }
            }

            long respTimeJaxws = 0;
            if (eventTrans.getResponseTimeLayerJaxws() != null) {
                respTimeJaxws = eventTrans.getResponseTimeLayerJaxws();
            }
            long respTimeJms = 0;
            if (eventTrans.getResponseTimeLayerJms() != null) {
                respTimeJms = eventTrans.getResponseTimeLayerJms();
            }
            long respTimeSql = 0;
            if (eventTrans.getResponseTimeLayerSql() != null) {
                respTimeSql = eventTrans.getResponseTimeLayerSql();
            }
            long responseTimeJava = eventTrans.getResponseTime() - (respTimeJaxws + respTimeJms + respTimeSql);
            eventTrans.setResponseTimeLayerJava(responseTimeJava);
        }

        public EventTransaction nullLayer(EventTransaction eventTrans) {
            if (eventTrans.getLayerNameSql() == null) {
                eventTrans.setResponseTimeLayerSql(0L);
                eventTrans.setLayerCountSql(0L);
                eventTrans.setLayerNameSql("null layer");
            }
            if (eventTrans.getLayerNameJaxws() == null) {
                eventTrans.setResponseTimeLayerJaxws(0L);
                eventTrans.setLayerCountJaxws(0L);
                eventTrans.setLayerNameJaxws("null layer");
            }

            if (eventTrans.getLayerNameJms() == null) {
                eventTrans.setLayerCountJms(0L);
                eventTrans.setResponseTimeLayerJms(0L);
                eventTrans.setLayerNameJms("null layer");
            }
            return eventTrans;
        }

        /**
         * Desription : parse datas in Json
         *
         * @param eventList
         * @return
         * @throws Exception
         * @Description :
         */
        public List<String> parseJson(List<EventTransaction> eventList) throws Exception {
            List<String> jsonEvents = new ArrayList<String>();
            ObjectMapper jsonSerializer = new ObjectMapper();

            if (config.getDryRun()) {
                jsonSerializer.enable(SerializationFeature.INDENT_OUTPUT);
            }
            for (NudgeEvent event : eventList) {
                // handle metadata
                String jsonMetadata = generateMetaData(event.getType());
                jsonEvents.add(jsonMetadata + lineBreak);
                // handle data event
                String jsonEvent = jsonSerializer.writeValueAsString(event);
                jsonEvents.add(jsonEvent + lineBreak);

            }
            LOG.debug(jsonEvents);
            return jsonEvents;
        }

        /**
         * Description : Permits to use API bulk to send huge rawdatas in
         * ElasticSearch To use this API it must be to format Json in the Bulk
         * Format.
         *
         * @param type
         * @return
         * @throws JsonProcessingException
         */
        public String generateMetaData(String type) throws JsonProcessingException {
            Configuration conf = new Configuration();
            ObjectMapper jsonSerializer = new ObjectMapper();
            if (config.getDryRun()) {
                jsonSerializer.enable(SerializationFeature.INDENT_OUTPUT);
            }
            BulkFormat elasticMetaData = new BulkFormat();
            elasticMetaData.getIndexElement().setIndex(conf.getElasticIndex());
            elasticMetaData.getIndexElement().setId(UUID.randomUUID().toString());
            elasticMetaData.getIndexElement().setType(type);
            return jsonSerializer.writeValueAsString(elasticMetaData);
        }

        /**
         * Description : It permits to index huge rawdata in elasticSearch with
         * HTTP request
         *
         * @param jsonEvents
         * @throws Exception
         */
        public void sendToElastic(List<String> jsonEvents) throws Exception {

            if (jsonEvents == null || jsonEvents.isEmpty()) {
                // LOG.info("No org.nudge.elasticstack.json documents to send");
                return;
            }
            Configuration conf = new Configuration();
            StringBuilder sb = new StringBuilder();
            for (String json : jsonEvents) {
                sb.append(json);
            }

            if (config.getDryRun()) {
                LOG.info("Dry run active, only log documents, don't push to elasticsearch");
                // LOG.info(sb);
                return;
            }

            long start = System.currentTimeMillis();
            URL URL = new URL(conf.getOutputElasticHosts() + "_bulk");
            if (LOG.isDebugEnabled()) {
                LOG.debug("Bulk request to : " + URL);
            }
            HttpURLConnection httpCon = (HttpURLConnection) URL.openConnection();
            httpCon.setDoOutput(true);
            httpCon.setRequestMethod("PUT");
            OutputStreamWriter out = new OutputStreamWriter(httpCon.getOutputStream());
            out.write(sb.toString());
            out.close();
            long end = System.currentTimeMillis();
            long totalTime = end - start;
            LOG.info(" Flush " + jsonEvents.size() + " documents in BULK insert in " + (totalTime / 1000f) + "sec");
            httpCon.getResponseCode();
            httpCon.getResponseMessage();
        }

    } // end of class

}

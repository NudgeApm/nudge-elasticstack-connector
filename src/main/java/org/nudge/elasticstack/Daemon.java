package org.nudge.elasticstack;

/**
 * Class which permits to send rawdatas to elasticSearch with -startDeamon
 *
 * @author Sarah Bourgeois
 * @author Sarah Bourgeois
 */

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

import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.log4j.Logger;
import org.nudge.elasticstack.config.Configuration;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nudge.apm.buffer.probe.RawDataProtocol.Layer;
import com.nudge.apm.buffer.probe.RawDataProtocol.RawData;
import com.nudge.apm.buffer.probe.RawDataProtocol.Transaction;
import json.bean.EventTransaction;
import json.bean.NudgeEvent;
import json.connection.Connection;

public class Daemon {

	private static final Logger LOG = Logger.getLogger(Daemon.class);

	private static ScheduledExecutorService scheduler;
	private static final String lineBreak = "\n";
	private static List<String> analyzedFilenames = new ArrayList<>();

	/**
	 * Description : Launcher Deamon.
	 *
	 * @param config
	 */
	static public void start(Configuration config) {
		scheduler = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
			public Thread newThread(Runnable runnable) {
				Thread thread = new Thread(runnable);
				thread.setName("nudge-es-daemon");
				thread.setDaemon(false);
				return thread;
			}
		});
		scheduler.scheduleAtFixedRate(new DaemonTask(config), 0L, 1L, TimeUnit.MINUTES);
	}

	static class DaemonTask implements Runnable {
		private Configuration config;

		public DaemonTask(Configuration config) {
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
					if (analyzedFilenames.size() == 0) {
						// analyzedFilenames.addAll(rawdataList);
					}
					// analyse files, comparaison and push
					for (String rawdataFilename : rawdataList) {
						if (!analyzedFilenames.contains(rawdataFilename)) {
							RawData rawdata = c.requestRawdata(appId, rawdataFilename);
							List<Transaction> transaction = rawdata.getTransactionsList();
							List<NudgeEvent> events = buildTransactionEvents(transaction);
							List<String> jsonEvents = parseJson(events);
							sendToElastic(jsonEvents);
						}
					}
					analyzedFilenames = rawdataList;
				}
			} catch (Throwable t) {
				t.printStackTrace();
				if (null != scheduler) {
					scheduler.shutdown();
				}
			}
		}

		/**
		 * Description : recuperate datas from rawdatas and add it to parse.
		 *
		 * @param transactionList
		 * @return
		 * @throws ParseException
		 * @throws JsonProcessingException
		 */
		public List<NudgeEvent> buildTransactionEvents(List<Transaction> transactionList)
				throws ParseException, JsonProcessingException {
			List<NudgeEvent> events = new ArrayList<NudgeEvent>();
			for (Transaction trans : transactionList) {
				String name = trans.getCode();
				SimpleDateFormat sdfr = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
				String date = sdfr.format(trans.getStartTime());
				long response_time = trans.getEndTime() - trans.getStartTime();
				EventTransaction transactionEvent = new EventTransaction(name, response_time, date, 1L);
				events.add(transactionEvent);

				addLayers(transactionEvent, trans.getLayersList());


				// handle layers
				buildLayerEvents(trans.getLayersList(), transactionEvent);
				events.add(transactionEvent);

				/*
				 * TODO Separate layer and transaction TortankLayer tortank =
				 * new TortankLayer(name, response_time, date, response_time);
				 * events.add(tortank);
				 */
			}
			if (events.size() != 0) {
				System.out.println("sum of events which will be send to elastic : " + events.size());
			} else {
				System.out.println("no new events to add now");
			}
			return events;
		}

		protected EventTransaction addLayers(EventTransaction transaction, List<Layer> rawdataLayers) {
			if (rawdataLayers != null && !rawdataLayers.isEmpty()) {
				for (Layer rawdataLayer : rawdataLayers) {
					json.bean.Layer layer = new json.bean.Layer(
							rawdataLayer.getLayerName(),
							rawdataLayer.getTime(),
							rawdataLayer.getCount()
					);
					if (transaction.getLayers() == null) {
						transaction.setLayers(new ArrayList<json.bean.Layer>());
					}
					transaction.getLayers().add(layer);
				}
			}
			return transaction;
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
					if (layer.getLayerName().equals("SQL") && layer.getLayerName() == null) {
						eventTrans.setLayerCountSql(null);
						eventTrans.setResponseTimeLayerSql(null);
						eventTrans.setLayerNameSql(null);
					}
				}
				if (layer.getLayerName().equals("JMS")) {
					eventTrans.setResponseTimeLayerJms(layer.getTime());
					eventTrans.setLayerCountJms(layer.getCount());
					eventTrans.setLayerNameJms(layer.getLayerName());
					if (layer.getLayerName().equals("JMS") && layer.getLayerName() == null) {
						eventTrans.setResponseTimeLayerJms(null);
						eventTrans.setLayerCountJms(null);
						eventTrans.setLayerNameJms(null);
					}
				}
				if (layer.getLayerName().equals("JAX-WS")) {
					eventTrans.setResponseTimeLayerJaxws(layer.getTime());
					eventTrans.setLayerCountJaxws(layer.getCount());
					eventTrans.setLayerNameJaxws(layer.getLayerName());
					if (layer.getLayerName().equals("JAX-WS") && layer.getLayerName() == null) {
						eventTrans.setResponseTimeLayerJaxws(null);
						eventTrans.setLayerCountJaxws(null);
						eventTrans.setLayerNameJaxws(null);
					}
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

		/**
		 *
		 * Desription : parse datas in Json
		 *
		 * @param eventList
		 * @return
		 * @throws Exception
		 * @Description :
		 */
		public List<String> parseJson(List<NudgeEvent> eventList) throws Exception {
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
			System.out.println(jsonEvents);
			return jsonEvents;
		}

		/**
		 *
		 * Description : Permits to use API bulk to send huge rawdatas in
		 * ElasticSearch To use this API it must be to format Json in the Bulk
		 * Format.
		 *
		 * @param type
		 * @return
		 * @throws JsonProcessingException
		 *
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
				LOG.info("No json documents to send");
				return;
			}
			Configuration conf = new Configuration();
			StringBuilder sb = new StringBuilder();
			for (String json : jsonEvents) {
				sb.append(json);
			}

			if (config.getDryRun()) {
				LOG.info("Dry run active, only log documents, don't push to elasticsearch");
				LOG.info(sb);
				return;
			}

			long start = System.currentTimeMillis();

			URL URL = new URL(conf.getElasticOutput() + "/_bulk");
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
			LOG.info("Flush " + jsonEvents.size() + " documents in BULK insert in " + (totalTime / 1000f) + "sec");
			LOG.info("Response : " + httpCon.getResponseCode() + " - " + httpCon.getResponseMessage());
		}


	} // end of class

	public static void main(String[] args) throws Exception {

		// to test, replace "********" to the right configuration
		System.setProperty("nes." + Configuration.NUDGE_APP_IDS, "*********");
		System.setProperty("nes." + Configuration.NUDGE_LOGIN, "*********");
		System.setProperty("nes." + Configuration.NUDGE_PWD, "*********");
		System.setProperty("nes." + Configuration.NUDGE_URL, "*********");
		System.setProperty("nes." + Configuration.ELASTIC_OUTPUT, "********");
		System.setProperty("nes." + Configuration.ELASTIC_INDEX, "********");
		Configuration config = new Configuration();
		DaemonTask task = new DaemonTask(config);
		task.run();
	}

}

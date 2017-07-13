package org.nudge.elasticstack.context.elasticsearch.builder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.log4j.Logger;
import org.nudge.elasticstack.context.elasticsearch.bean.BulkFormat;
import org.nudge.elasticstack.Configuration;
import org.nudge.elasticstack.context.elasticsearch.bean.EventTransaction;
import org.nudge.elasticstack.context.elasticsearch.bean.NudgeEvent;
import org.nudge.elasticstack.context.nudge.dto.LayerDTO;
import org.nudge.elasticstack.context.nudge.dto.TransactionDTO;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Serialize Nudge transaction into JSON object.
 */
public class TransactionSerializer {

	private static final Logger LOG = Logger.getLogger(TransactionSerializer.class.getName());
	private static final String lineBreak = "\n";
	private Configuration config = Configuration.getInstance();

	/**
	 * Retrieve transaction data from rawdata and add it to parse.
	 * @param appId 
	 * @param transactionList
	 * @return
	 * @throws ParseException
	 * @throws JsonProcessingException
	 */
	public List<EventTransaction> serialize(String appId, List<TransactionDTO> transactionList)
			throws ParseException, JsonProcessingException {
		List<EventTransaction> events = new ArrayList<EventTransaction>();
		for (TransactionDTO trans : transactionList) {
			String name = trans.getCode();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
			String date = sdf.format(trans.getStartTime());
			long responseTime = trans.getEndTime() - trans.getStartTime();
			EventTransaction transactionEvent = new EventTransaction(appId, name, responseTime, date, 1L, trans.getId());
			events.add(transactionEvent);
			// handle layers
			buildLayerEvents(trans.getLayers(), transactionEvent);
			events.add(transactionEvent);
		}
		return events;
	}

	/**
	 * Retrieve layer from transaction
	 *
	 * @param eventTrans
	 * @return
	 */
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
	 * Description : build layer events
	 *
	 * @param rawdataLayers
	 * @param eventTrans
	 * @throws ParseException
	 * @throws JsonProcessingException
	 */
	public void buildLayerEvents(List<LayerDTO> rawdataLayers, EventTransaction eventTrans)
			throws ParseException, JsonProcessingException {
		for (LayerDTO layer : rawdataLayers) {
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

	/**
	 * Desription : parse datas in Json
	 *
	 * @param eventList
	 * @return
	 * @throws Exception
	 * @Description :
	 */
	public List<String> serialize(List<EventTransaction> eventList) throws Exception {
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
//		LOG.debug(jsonEvents);
		return jsonEvents;
	}

	/**
	 * Description : Use bulk API to send huge rawdatas in ElasticSearch To use
	 * this API it must be to format Json in the Bulk Format.
	 *
	 * @param type
	 * @return
	 * @throws JsonProcessingException
	 */
	public String generateMetaData(String type) throws JsonProcessingException {
		ObjectMapper jsonSerializer = new ObjectMapper();
		if (config.getDryRun()) {
			jsonSerializer.enable(SerializationFeature.INDENT_OUTPUT);
		}
		BulkFormat elasticMetaData = new BulkFormat();
		elasticMetaData.getIndexElement().setId(UUID.randomUUID().toString());
		elasticMetaData.getIndexElement().setIndex(config.getElasticIndex());
		elasticMetaData.getIndexElement().setType(type);
		return jsonSerializer.writeValueAsString(elasticMetaData);
	}

	/**
	 * Description : It permits to index huge rawdata in elasticSearch with HTTP
	 * request
	 * @param jsonEvents
	 * @throws Exception
	 */
	public void sendToElastic(List<String> jsonEvents) throws Exception {
		if (jsonEvents == null || jsonEvents.isEmpty()) {
			return;
		}
		StringBuilder sb = new StringBuilder();
		for (String json : jsonEvents) {
			sb.append(json);
		}
		if (config.getDryRun()) {
			LOG.info("Dry run active, only log documents, don't push to elasticsearch");
			return;
		}
		long start = System.currentTimeMillis();
		URL URL = new URL(config.getOutputElasticHosts() + "_bulk");
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

}

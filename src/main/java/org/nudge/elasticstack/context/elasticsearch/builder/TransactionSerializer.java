package org.nudge.elasticstack.context.elasticsearch.builder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.log4j.Logger;
import org.nudge.elasticstack.Configuration;
import org.nudge.elasticstack.context.elasticsearch.bean.BulkFormat;
import org.nudge.elasticstack.context.elasticsearch.bean.EventType;
import org.nudge.elasticstack.context.elasticsearch.bean.LayerEvent;
import org.nudge.elasticstack.context.elasticsearch.bean.NudgeEvent;
import org.nudge.elasticstack.context.elasticsearch.bean.TransactionEvent;
import org.nudge.elasticstack.context.nudge.dto.LayerCallDTO;
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
	public List<NudgeEvent> serialize(String appId, String appName, String host, String hostname, List<TransactionDTO> transactionList)
			throws ParseException, JsonProcessingException {
		List<NudgeEvent> events = new ArrayList<>();
		for (TransactionDTO trans : transactionList) {
			TransactionEvent transactionEvent = new TransactionEvent();

			// build the transaction JSON object
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
			String date = sdf.format(trans.getStartTime());
			transactionEvent.setAppId(appId);
			transactionEvent.setAppName(appName);
			transactionEvent.setHost(host);
			transactionEvent.setHostname(hostname);
			transactionEvent.setName(trans.getCode());
			transactionEvent.setResponseTime(trans.getEndTime() - trans.getStartTime());
			transactionEvent.setDate(date);
			transactionEvent.setCount(1L);
			transactionEvent.setTransactionId(trans.getId());
			events.add(transactionEvent);

			// handle layers - build for each layers a JSON object
			events.addAll(buildLayerEvents(trans.getLayers(), transactionEvent));
		}
		return events;
	}

	/**
	 * Retrieve layer from transaction
	 *
	 * @param eventTrans
	 * @return
	 */
	public TransactionEvent nullLayer(TransactionEvent eventTrans) {
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


	protected LayerEvent createLayerEvent(EventType type, LayerCallDTO layerCallDTO, TransactionEvent transaction) {

		SimpleDateFormat sdfr = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

		String sqlTimestamp = sdfr.format(layerCallDTO.getTimestamp());

		LayerEvent layerEvent = new LayerEvent(type);
		layerEvent.setAppId(transaction.getAppId());
		layerEvent.setAppName(transaction.getAppName());
		layerEvent.setHost(transaction.getHost());
		layerEvent.setHostname(transaction.getHostname());
		layerEvent.setDate(sqlTimestamp);
		layerEvent.setName(layerCallDTO.getCode());
		layerEvent.setCount(layerCallDTO.getCount());
		layerEvent.setResponseTime(layerCallDTO.getResponseTime());
		layerEvent.setTransactionId(transaction.getTransactionId());

		return layerEvent;
	}

	protected LayerEvent createJavaLayerEvent(TransactionEvent transaction, long responseTime) {
		LayerEvent javaLayer = new LayerEvent(EventType.JAVA);
		javaLayer.setAppId(transaction.getAppId());
		javaLayer.setAppName(transaction.getAppName());
		javaLayer.setHost(transaction.getHost());
		javaLayer.setHostname(transaction.getHostname());
		javaLayer.setDate(transaction.getDate());
		javaLayer.setName(transaction.getName());
		javaLayer.setCount(transaction.getCount());
		javaLayer.setResponseTime(responseTime);
		javaLayer.setTransactionId(transaction.getTransactionId());
		return javaLayer;
	}

	/**
	 * Build layer events
	 *
	 * @param rawdataLayers
	 * @param transactionEvent
	 * @throws ParseException
	 * @throws JsonProcessingException
	 */
	public List<LayerEvent> buildLayerEvents(List<LayerDTO> rawdataLayers, TransactionEvent transactionEvent)
			throws ParseException, JsonProcessingException {

		List<LayerEvent> layers = new ArrayList<>();

		// TODO split transaction affectation and layers...
		for (LayerDTO layer : rawdataLayers) {
			if (layer.getLayerName().equals("SQL")) {
				transactionEvent.setResponseTimeLayerSql(layer.getTime());
				transactionEvent.setLayerCountSql(layer.getCount());
				transactionEvent.setLayerNameSql(layer.getLayerName());
				for (LayerCallDTO layerCallDTO : layer.getCalls()) {
					layers.add(createLayerEvent(EventType.SQL, layerCallDTO, transactionEvent));
				}
			}
			if (layer.getLayerName().equals("JMS")) {
				transactionEvent.setResponseTimeLayerJms(layer.getTime());
				transactionEvent.setLayerCountJms(layer.getCount());
				transactionEvent.setLayerNameJms(layer.getLayerName());
				for (LayerCallDTO layerCallDTO : layer.getCalls()) {
					layers.add(createLayerEvent(EventType.JMS, layerCallDTO, transactionEvent));
				}
			}
			if (layer.getLayerName().equals("JAX-WS")) {
				transactionEvent.setResponseTimeLayerJaxws(layer.getTime());
				transactionEvent.setLayerCountJaxws(layer.getCount());
				transactionEvent.setLayerNameJaxws(layer.getLayerName());
				for (LayerCallDTO layerCallDTO : layer.getCalls()) {
					layers.add(createLayerEvent(EventType.JAX_WS, layerCallDTO, transactionEvent));
				}
			}
			if (layer.getLayerName().equals("JAVA")) {
				transactionEvent.setLayerCountJava(layer.getCount());
				transactionEvent.setLayerNameJava(layer.getLayerName());
				for (LayerCallDTO layerCallDTO : layer.getCalls()) {
					layers.add(createLayerEvent(EventType.JAVA, layerCallDTO, transactionEvent));
				}
			}
		}
		// compute the java layer response time
		long totalLayerTime = 0;
		if (transactionEvent.getResponseTimeLayerJaxws() != null) {
			totalLayerTime = totalLayerTime + transactionEvent.getResponseTimeLayerJaxws();
		}
		if (transactionEvent.getResponseTimeLayerJms() != null) {
			totalLayerTime = totalLayerTime + transactionEvent.getResponseTimeLayerJms();
		}
		if (transactionEvent.getResponseTimeLayerSql() != null) {
			totalLayerTime = totalLayerTime + transactionEvent.getResponseTimeLayerSql();
		}
		transactionEvent.setResponseTimeLayerJava(transactionEvent.getResponseTime() - totalLayerTime);
		layers.add(createJavaLayerEvent(transactionEvent, transactionEvent.getResponseTimeLayerJava()));

		return layers;
	}

	/**
	 * Parse datas in Json
	 *
	 * @param eventList
	 * @return
	 * @throws Exception
	 */
	public List<String> serialize(List<NudgeEvent> eventList) throws Exception {
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
	 * Use bulk API to send huge rawdatas in ElasticSearch To use
	 * this API it must be to format Json in the Bulk Format.
	 *
	 * @param type
	 * @return
	 * @throws JsonProcessingException
	 */
	public String generateMetaData(EventType type) throws JsonProcessingException {
		ObjectMapper jsonSerializer = new ObjectMapper();
		if (config.getDryRun()) {
			jsonSerializer.enable(SerializationFeature.INDENT_OUTPUT);
		}
		BulkFormat elasticMetaData = new BulkFormat();
		elasticMetaData.getIndexElement().setId(UUID.randomUUID().toString());
		elasticMetaData.getIndexElement().setIndex(config.getElasticIndex());
		elasticMetaData.getIndexElement().setType(type.toString());
		return jsonSerializer.writeValueAsString(elasticMetaData);
	}

	/**
	 * It permits to index huge rawdata in elasticSearch with HTTP
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
		URL URL = new URL(config.getElasticHostURL() + "_bulk");
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
		LOG.info(" Flush " + jsonEvents.size() + " documents in BULK insert in " + (totalTime / 1000f) + "sec : "
				+ httpCon.getResponseCode() + " - " + httpCon.getResponseMessage());
	}

}

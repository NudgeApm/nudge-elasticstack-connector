package org.nudge.elasticstack.context.elasticsearch.builder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.log4j.Logger;
import org.nudge.elasticstack.Configuration;
import org.nudge.elasticstack.context.elasticsearch.bean.BulkFormat;
import org.nudge.elasticstack.context.elasticsearch.bean.SQLEvent;
import org.nudge.elasticstack.context.nudge.dto.LayerCallDTO;
import org.nudge.elasticstack.context.nudge.dto.LayerDTO;
import org.nudge.elasticstack.context.nudge.dto.TransactionDTO;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class SQLLayer {

	private static final Logger LOG = Logger.getLogger(SQLLayer.class.getName());
	private static final String lineBreak = "\n";
	private Configuration config = Configuration.getInstance();

	/**
	 * Extract SQL events from transactions.
	 * @param appId 
	 */
	public List<SQLEvent> buildSQLEvents(String appId, String host, String hostname, List<TransactionDTO> transactions) {
		List<SQLEvent> sqlEvents = new ArrayList<>();

		for (TransactionDTO transaction : transactions) {
			for (LayerDTO layer : transaction.getLayers()) {
				for (LayerCallDTO layerCall : layer.getCalls()) {
					SimpleDateFormat sdfr = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
					String sqlTimestamp = sdfr.format(layerCall.getTimestamp());

					SQLEvent sqlEvent = new SQLEvent();
					sqlEvent.setAppId(appId);
					sqlEvent.setHost(host);
					sqlEvent.setHostname(hostname);
					sqlEvent.setDate(sqlTimestamp);
					sqlEvent.setName(layerCall.getCode());
					sqlEvent.setCount(layerCall.getCount());
					sqlEvent.setResponseTime(layerCall.getResponseTime());
					sqlEvent.setTransactionId(transaction.getId());
					sqlEvents.add(sqlEvent);
				}
			}
		}
		return sqlEvents;
	}

	/**
	 * Parse SQL to send to Elastic
	 *
	 * @param sqlEvents
	 * @return
	 * @throws Exception
	 */
	public List<String> parseJsonSQL(List<SQLEvent> sqlEvents) throws Exception {
		List<String> jsonEventsSql = new ArrayList<String>();
		ObjectMapper jsonSerializer = new ObjectMapper();

		if (config.getDryRun()) {
			jsonSerializer.enable(SerializationFeature.INDENT_OUTPUT);
		}
		for (SQLEvent sqlEvent : sqlEvents) {
			String jsonMetadata = generateMetaDataSQL(sqlEvent.getName());
			jsonEventsSql.add(jsonMetadata + lineBreak);
			// Handle data sqlEvent
			String jsonEvent = jsonSerializer.writeValueAsString(sqlEvent);
			jsonEventsSql.add(jsonEvent + lineBreak);
		}
		return jsonEventsSql;
	}

	/**
	 * Generate SQL for Bulk api
	 *
	 * @param sql
	 * @return
	 * @throws JsonProcessingException
	 */
	public String generateMetaDataSQL(String sql) throws JsonProcessingException {
		ObjectMapper jsonSerializer = new ObjectMapper();
		if (config.getDryRun()) {
			jsonSerializer.enable(SerializationFeature.INDENT_OUTPUT);
		}
		BulkFormat elasticMetaData = new BulkFormat();
		elasticMetaData.getIndexElement().setIndex(config.getElasticIndex());
		elasticMetaData.getIndexElement().setType("sql");
		return jsonSerializer.writeValueAsString(elasticMetaData);
	}

	/**
	 * Send MBean into elasticSearch
	 *
	 * @param jsonEventsSql
	 * @throws IOException
	 */
	public void sendSqltoElk(List<String> jsonEventsSql) throws IOException {
		StringBuilder sb = new StringBuilder();
		for (String json : jsonEventsSql) {
			sb.append(json);
		}
		if (config.getDryRun()) {
			LOG.debug("Dry run active, only log documents, don't push to elasticsearch.");
			return;
		}
		long start = System.currentTimeMillis();
		URL URL = new URL(config.getOutputElasticHosts() + "_bulk");
		if (LOG.isDebugEnabled()) {
			LOG.debug("Bulk request to : " + URL);
		}
		HttpURLConnection httpCon2 = (HttpURLConnection) URL.openConnection();
		httpCon2.setDoOutput(true);
		httpCon2.setRequestMethod("PUT");
		OutputStreamWriter out = new OutputStreamWriter(httpCon2.getOutputStream());
		out.write(sb.toString());
		out.close();
		long end = System.currentTimeMillis();
		long totalTime = end - start;
		LOG.info(" Flush " + jsonEventsSql.size() + " documents insert in BULK in : " + (totalTime / 1000f) + "sec");
		LOG.debug(" Sending Sql : " + httpCon2.getResponseCode() + " - " + httpCon2.getResponseMessage());
	}

}
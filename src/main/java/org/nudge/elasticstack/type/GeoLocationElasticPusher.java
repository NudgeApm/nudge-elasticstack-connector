package org.nudge.elasticstack.type;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.log4j.Logger;
import org.nudge.elasticstack.BulkFormat;
import org.nudge.elasticstack.config.Configuration;
import org.nudge.elasticstack.json.bean.GeoLocation;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Sarah Bourgeois
 * @author Frederic Massart
 */
public class GeoLocationElasticPusher {

	private static final Logger LOG = Logger.getLogger(GeoLocationElasticPusher.class);
	private static final String lineBreak = "\n";
	Configuration config = new Configuration();

	/**
	 * Description : pusher to elastic
	 * @param jsonEvent
	 */
		public List<GeoLocationWriter> pushGeoLocation(List<GeoLocation> jsonEvent) {
			
		List<String> geoEvents = new ArrayList<>();
	
		for (GeoLocation geoLocation : jsonEvent) { 
			try {
			
			
				geoEvents.addAll(parseJsonUserIp(geoLocation));
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}
		try {
			sendElk(geoEvents);
		} catch (IOException e) {
			LOG.error("Error while pushing geo location object to elastic", e);
		}
		return null;
	}

		/**
		 * Descri
		 * @param geoLocation
		 * @return
		 * @throws JsonProcessingException
		 */
	protected List<String> parseJsonUserIp(GeoLocation geoLocation) throws JsonProcessingException {
		List<String> jsonEvent = new ArrayList<>();
		ObjectMapper jsonSerializer = new ObjectMapper();
		if (config.getDryRun()) {
			jsonSerializer.enable(SerializationFeature.INDENT_OUTPUT);
		}
		String jsonMetadata = generateMetaDataUserIp();
		jsonEvent.add(jsonMetadata + lineBreak);
		// handle data event
		String jsonevent = jsonSerializer.writeValueAsString(geoLocation);
		jsonEvent.add(jsonevent + lineBreak);
		LOG.debug(jsonEvent);
		return jsonEvent;
	}

	protected String generateMetaDataUserIp() throws JsonProcessingException {
		Configuration conf = new Configuration();
		ObjectMapper jsonSerializer = new ObjectMapper();
		if (config.getDryRun()) {
			jsonSerializer.enable(SerializationFeature.INDENT_OUTPUT);
		}
		BulkFormat elasticMetaData = new BulkFormat();
		elasticMetaData.getIndexElement().setIndex(conf.getElasticIndex());
		elasticMetaData.getIndexElement().setId(UUID.randomUUID().toString());
		elasticMetaData.getIndexElement().setType("geolocation");
		return jsonSerializer.writeValueAsString(elasticMetaData);
	}

	private void sendElk(List<String> jsonEvent) throws IOException {
		Configuration conf = new Configuration();
		StringBuilder sb = new StringBuilder();
		for (String json : jsonEvent) {
			sb.append(json);
		}
		if (config.getDryRun()) {
			LOG.info("Dry run active, only log documents, don't push to elasticsearch.");
			return;
		}
		long start = System.currentTimeMillis();
		URL URL = new URL(conf.getOutputElasticHosts() + "_bulk");
		if (LOG.isDebugEnabled()) {
			LOG.debug("Bulk request to : " + URL);
		}
		HttpURLConnection httpCon2 = (HttpURLConnection) URL.openConnection();
		httpCon2.setDoOutput(true);
		httpCon2.setRequestMethod("PUT");
		OutputStreamWriter out = new OutputStreamWriter(httpCon2.getOutputStream());
		out.write(sb.toString());
		System.out.println(sb);
		out.close();
		long end = System.currentTimeMillis();
		long totalTime = end - start;
		LOG.info(" Flush " + jsonEvent.size() + " documents insert in BULK in : " + (totalTime / 1000f) + "sec");
		LOG.debug(" Sending Location-Map: " + httpCon2.getResponseCode() + " - " + httpCon2.getResponseMessage());
	}

} // End of class

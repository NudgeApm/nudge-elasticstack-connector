package org.nudge.elasticstack.context.elasticsearch.json.builder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.log4j.Logger;
import org.nudge.elasticstack.BulkFormat;
import org.nudge.elasticstack.Configuration;
import org.nudge.elasticstack.context.elasticsearch.json.bean.GeoLocation;
import org.nudge.elasticstack.context.elasticsearch.json.bean.GeoLocationWriter;
import org.nudge.elasticstack.context.nudge.dto.TransactionDTO;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Sarah Bourgeois
 * @author Frederic Massart
 */

public class GeoLocationElasticPusher {

	private static final Logger LOG = Logger.getLogger(GeoLocationElasticPusher.class.getName());
	private static final String lineBreak = "\n";
	private Configuration config = Configuration.getInstance();

	/**
	 * Description : Retrieve data from List Geolocalisation and write it in a
	 * new bean
	 * 
	 * @param geolocation
	 * @param transaction
	 * @return
	 */
	public List<GeoLocationWriter> buildLocationEvents(List<GeoLocation> geolocation, List<TransactionDTO> transaction) {
		List<GeoLocationWriter> geowriter = new ArrayList<>();
		for (GeoLocation geo : geolocation) {
			String timestamp = null;
			double latitude = geo.getLatitude();
			double longitude = geo.getLongitude();
			String Location = geo.getClientlocation();
			String id = "";
			GeoLocationWriter geolocationwriter = new GeoLocationWriter(latitude, longitude, Location, "location",
					timestamp, id);
			for (TransactionDTO trans : transaction) {
				SimpleDateFormat sdfr = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
				String date = sdfr.format(trans.getStartTime());
				timestamp = geolocationwriter.setResponseTime(date);
			}
			geowriter.add(geolocationwriter);
		}
		return geowriter;
	}

	/**
	 *  
	 * @param eventList
	 * @return
	 * @throws JsonProcessingException
	 */
	public List<String> parseJsonLocation(List<GeoLocationWriter> eventList) throws JsonProcessingException {
		List<String> jsonEvent2 = new ArrayList<String>();
		ObjectMapper jsonSerializer = new ObjectMapper();
		if (config.getDryRun()) {
			jsonSerializer.enable(SerializationFeature.INDENT_OUTPUT);
		}
		for (GeoLocationWriter event : eventList) {
			String jsonMetadata = generateMetaDataGeoLocation(event.getType());
			jsonEvent2.add(jsonMetadata + lineBreak);
			// Handle data event
			String jsonEvent = jsonSerializer.writeValueAsString(event);
			jsonEvent2.add(jsonEvent + lineBreak);
		}
		LOG.debug(jsonEvent2);
		return jsonEvent2;
	}

	/**
	 * Description : generate Geolocation data for Bulk api
	 * 
	 * @param mbean
	 * @return
	 * @throws JsonProcessingException
	 */
	public String generateMetaDataGeoLocation(String mbean) throws JsonProcessingException {
		ObjectMapper jsonSerializer = new ObjectMapper();
		if (config.getDryRun()) {
			jsonSerializer.enable(SerializationFeature.INDENT_OUTPUT);
		}
		BulkFormat elasticMetaData = new BulkFormat();
		elasticMetaData.getIndexElement().setIndex(config.getElasticIndex());
		elasticMetaData.getIndexElement().setType("location");
		return jsonSerializer.writeValueAsString(elasticMetaData);
	}

	/**
	 * Description : Send Geolocation data into elasticSearch
	 *
	 * @param jsonEvents2
	 * @throws IOException
	 */
	public void sendElk(List<String> jsonEvents2) throws IOException {
		StringBuilder sb = new StringBuilder();

		for (String json : jsonEvents2) {
			sb.append(json);
		}
		if (config.getDryRun()) {
			LOG.info("Dry run active, only log documents, don't push to elasticsearch.");
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
		LOG.info(" Flush " + jsonEvents2.size() + " documents insert in BULK in : " + (totalTime / 1000f) + "sec");
		LOG.debug(" Sending MBean : " + httpCon2.getResponseCode() + " - " + httpCon2.getResponseMessage());
	}
	


}

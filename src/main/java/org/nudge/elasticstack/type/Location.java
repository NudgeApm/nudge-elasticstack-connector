package org.nudge.elasticstack.type;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.apache.log4j.Logger;
import org.nudge.elasticstack.BulkFormat;
import org.nudge.elasticstack.config.Configuration;
import org.nudge.elasticstack.json.bean.EventGeoLocation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.nudge.apm.buffer.probe.RawDataProtocol.Transaction;

/**
 * 
 * @author Sarah Bourgeois Frederic Massart
 *
 */

public class Location {
	private static final Logger LOG = Logger.getLogger("Location org.nudge.elasticstack.type : ");
	private static final String lineBreak = "\n";
	Configuration config = new Configuration();

	/**
	 * 
	 * @param transaction
	 * @return
	 * @throws IOException
	 */
	public List<EventGeoLocation> buildLocationEvent(List<Transaction> transaction) throws IOException {
		List<EventGeoLocation> eventGeolocations = new ArrayList<>();
		for (Transaction trans : transaction) {
			String userIp = trans.getUserIp();
			EventGeoLocation userIpEvent = new EventGeoLocation(getGeoFromIP(userIp), "location");
			eventGeolocations.add(userIpEvent);
		}
		return eventGeolocations;
	}

	public List<String> parseJsonUserIp(List<EventGeoLocation> eventList) throws JsonProcessingException {
		List<String> jsonEvent = new ArrayList<>();
		ObjectMapper jsonSerializer = new ObjectMapper();
		if (config.getDryRun()) {
			jsonSerializer.enable(SerializationFeature.INDENT_OUTPUT);
		}
		for (EventGeoLocation event : eventList) {
			String jsonMetadata = generateMetaDataUserIp(event.getType());
			jsonEvent.add(jsonMetadata + lineBreak);
			// handle data event
			String jsonevent = jsonSerializer.writeValueAsString(event);
			jsonEvent.add(jsonevent + lineBreak);
		}
		LOG.debug(jsonEvent);
		System.out.println(jsonEvent);
		return jsonEvent;
	}

	public String generateMetaDataUserIp(String userIp) throws JsonProcessingException {
		Configuration conf = new Configuration();
		ObjectMapper jsonSerializer = new ObjectMapper();
		if (config.getDryRun()) {
			jsonSerializer.enable(SerializationFeature.INDENT_OUTPUT);
		}
		BulkFormat elasticMetaData = new BulkFormat();
		elasticMetaData.getIndexElement().setIndex(conf.getElasticIndex());
		elasticMetaData.getIndexElement().setId(UUID.randomUUID().toString());
		elasticMetaData.getIndexElement().setType("location");
		return jsonSerializer.writeValueAsString(elasticMetaData);
	}

	public void sendElk(List<String> jsonEvent) throws IOException {
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
		out.close();
		long end = System.currentTimeMillis();
		long totalTime = end - start;
		LOG.info(" Flush " + jsonEvent.size() + " documents insert in BULK in : " + (totalTime / 1000f) + "sec");
		LOG.debug(" Sending Location-Map: " + httpCon2.getResponseCode() + " - " + httpCon2.getResponseMessage());
	}

	// requeter l'api de conversion
	private static HttpURLConnection prepareGeoDataRequest(String completeUrl) {
		try {
			URL loginUrl = new URL(completeUrl);
			HttpURLConnection con = (HttpURLConnection) loginUrl.openConnection();
			con.setRequestMethod("GET");
			con.setConnectTimeout(1000);
			con.setReadTimeout(5000);
			con.setInstanceFollowRedirects(false);
			return con;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	// Methode de recuperer des contenus de l'api selon une adresse IP
	public static List<String> getGeoFromIP(String userIp) throws IOException {
		List<String> contentRawdata = new ArrayList<>();
		String finalUrl = "http://freegeoip.net/json/" + userIp;
		HttpURLConnection connection = prepareGeoDataRequest(finalUrl);
		LOG.debug(connection.getResponseCode());
		String contentRawdataList = convertStreamToString(connection.getInputStream());
		connection.disconnect();
		contentRawdata.add(contentRawdataList);
		return contentRawdata;
	}

	// methode qui permet de convertir l'inputStream en Str
	 protected List<String> parseUserIpResponse(InputStream stream) {
	 List<String> content = new ArrayList<String>();
	String var = convertStreamToString(stream);
		String var2 = var.substring(1, var.length() - 1);
		if (var2.length() == 0) {
			return content;
		}		
		String[] var3 = var2.split(",");
		for (String s : var3) {
			String s1 = s.substring(1, s.length() - 1);
			content.add(s1);
		}
	 return content;
	 }

	// Methode qui converti les inputstream en string
		private static String convertStreamToString(java.io.InputStream is) {
			try (java.util.Scanner s = new java.util.Scanner(is)) {
				s.useDelimiter("\\A");
				return s.hasNext() ? s.next() : "";
			}
		}

	


} // End of class

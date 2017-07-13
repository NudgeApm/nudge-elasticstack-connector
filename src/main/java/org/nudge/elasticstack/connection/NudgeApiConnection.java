  package org.nudge.elasticstack.connection;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nudge.apm.buffer.probe.RawDataProtocol.RawData;
import org.apache.log4j.Logger;
import org.nudge.elasticstack.Configuration;
import org.nudge.elasticstack.context.nudge.filter.bean.Filter;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Connection to the Nudge API.
 *
 * @author : Sarah Bourgeois.
 * @author : Frederic Massart
*/
public class NudgeApiConnection {
	private static final Logger LOG = Logger.getLogger(NudgeApiConnection.class);
	private final String url;
	private final String apiKeyParam;
	private Configuration config = Configuration.getInstance();

	public NudgeApiConnection(String url, String apiKey) {
		this.url = url;
		this.apiKeyParam = "Bearer " + apiKey;
	}

	public List<String> getRawdataList(String appId, String from) throws IOException {
		String finalUrl = url + "api/apps/" + appId + "/rawdata?from=" + config.getRawdataHistory();
		HttpURLConnection connection = prepareRequest(finalUrl);
		LOG.debug("Request URL " + finalUrl + " = " + connection.getResponseCode());
		List<String> contentRawdata = parseRawdataListResponse(connection.getInputStream());
		connection.disconnect();
		return contentRawdata;
	}

	/**
	 * Parse a stream and return a list of rawdata names.
	 *
	 * @param stream
	 *            the stream response, contains a string of rawdata names
	 * @return the list of rawdata anmes
	 */
	protected List<String> parseRawdataListResponse(InputStream stream) {
		List<String> contentRawdata = new ArrayList<String>();
		String var = convertStreamToString(stream);
		String var2 = var.substring(1, var.length() - 1);
		if (var2.length() == 0) {
			return contentRawdata;
		}		
		String[] var3 = var2.split(",");
		for (String s : var3) {
			String s1 = s.substring(1, s.length() - 1);
			contentRawdata.add(s1);
		}
		return contentRawdata; 
	}

	private static String convertStreamToString(java.io.InputStream is) {
		try (java.util.Scanner s = new java.util.Scanner(is)) {
			s.useDelimiter("\\A");
			return s.hasNext() ? s.next() : "";
		}
	}

	public RawData getRawdata(String appId, String rawdataFilename) throws IOException {
		String finalUrl = url + "api/apps/" + appId + "/rawdata/" + rawdataFilename;
		HttpURLConnection connection = prepareRequest(finalUrl);
		return RawData.parseFrom(connection.getInputStream());
	}

	private HttpURLConnection prepareRequest(String completeUrl) {
		try {
			URL loginUrl = new URL(completeUrl);
			HttpURLConnection con = (HttpURLConnection) loginUrl.openConnection();
			con.setRequestMethod("GET");
			con.setConnectTimeout(1000);
			con.setReadTimeout(5000);
			con.setInstanceFollowRedirects(false);
			con.setRequestProperty("Authorization", apiKeyParam);
			return con;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public List<Filter> requestFilters(String appId) throws IOException {
		String finalUrl = url + "api/apps/" + appId + "/filters";
		HttpURLConnection connection = prepareRequest(finalUrl);

		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
		mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);

		return mapper.readValue(connection.getInputStream(), new TypeReference<List<Filter>>(){});
	}
}

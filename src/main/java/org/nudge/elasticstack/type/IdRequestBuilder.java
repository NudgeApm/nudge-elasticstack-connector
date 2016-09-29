package org.nudge.elasticstack.type;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.nudge.elasticstack.config.Configuration;
import org.nudge.elasticstack.json.bean.GeoLocation;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;


public class IdRequestBuilder {

	Configuration conf = new Configuration();
	
	public Sql requestIdFromElastic(String ip) throws IOException {
		String geoLocString = getGeoFromIP(ip);
		return parseId(geoLocString);
	}

	/**
	 *
	 * @param completeUrl
	 * @return
	 */
	private HttpURLConnection prepareIdRequest(String completeUrl) {
		try {
			URL url = new URL(completeUrl);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.setConnectTimeout(1000);
			con.setReadTimeout(5000);
			con.setInstanceFollowRedirects(false);
			return con;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private Sql parseId(String geoLocationString) {
		Sql sql = new Sql();
		if (geoLocationString != null && !geoLocationString.equals("")) {
			ObjectMapper mapper = new ObjectMapper();
			try {
				MappingIterator<Sql> objectMappingIterator = mapper.reader().forType(GeoLocation.class)
						.readValues(geoLocationString);
				sql = objectMappingIterator.next();
			} catch (IOException e) {
			//	Logger.error("Can't parse the geo json object from api", e);
			}
		}
		return sql;
	}

	/**
	 * Description : Retrieve api data
	 * 	
	 * @param userIp
	 * @return
	 * @throws IOException
	 */
	private String getGeoFromIP(String userIp) throws IOException {
		HttpURLConnection connection = prepareIdRequest(conf.getNudgeUrl() + "api/apps" + conf.getAppIds() + "/rawdata?from=" + conf.getRawdataHistory());
		//LOG.debug(connection.getResponseCode());
		InputStream is = connection.getInputStream();
		String geoString = convertStreamToString(is);
		connection.disconnect();
		return geoString;
	}

	/**
	 * Description : convert inputStream in String
	 * 
	 * @param is
	 * @return
	 */
	private String convertStreamToString(java.io.InputStream is) {
		try (java.util.Scanner s = new java.util.Scanner(is)) {
			s.useDelimiter("\\A");
			return s.hasNext() ? s.next() : "";
		}
	}
	
	
	
	
	
}

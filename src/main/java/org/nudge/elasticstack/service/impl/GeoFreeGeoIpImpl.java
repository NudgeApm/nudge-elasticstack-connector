package org.nudge.elasticstack.service.impl;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.nudge.elasticstack.json.bean.GeoLocation;
import org.nudge.elasticstack.service.GeoLocationService;
import org.nudge.elasticstack.type.GeoLocationWriter;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sarah Bourgeois
 * @author Frederic Massart
 */
public class GeoFreeGeoIpImpl implements GeoLocationService {

	private static final Logger LOG = Logger.getLogger(GeoFreeGeoIpImpl.class);
	private static final String FINAL_URL = "http://freegeoip.net/json/";

	@Override
	public GeoLocation requestGeoLocationFromIp(String ip) throws IOException {
		String geoLocString = getGeoFromIP(ip);
		return parseGeoLocation(geoLocString);
	}

	/**
	 *
	 * @param completeUrl
	 * @return
	 */
	private HttpURLConnection prepareGeoDataRequest(String completeUrl) {
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

	private GeoLocation parseGeoLocation(String geoLocationString) {
		GeoLocation geoLocation = new GeoLocation();
		if (geoLocationString != null && !geoLocationString.equals("")) {
			ObjectMapper mapper = new ObjectMapper();
			try {
				MappingIterator<GeoLocation> objectMappingIterator = mapper.reader().forType(GeoLocation.class)
						.readValues(geoLocationString);
				geoLocation = objectMappingIterator.next();
			} catch (IOException e) {
				LOG.error("Can't parse the geo json object from api", e);
			}
		}
		return geoLocation;
	}

	/**
	 * Description : Retrieve api data
	 * 
	 * @param userIp
	 * @return
	 * @throws IOException
	 */
	private String getGeoFromIP(String userIp) throws IOException {
		HttpURLConnection connection = prepareGeoDataRequest(FINAL_URL + userIp);
		LOG.debug(connection.getResponseCode());
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

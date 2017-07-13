package org.nudge.elasticstack.service.impl;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.nudge.elasticstack.exception.NudgeESConnectorException;
import org.nudge.elasticstack.context.elasticsearch.bean.GeoLocation;
import org.nudge.elasticstack.service.GeoLocationService;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author Sarah Bourgeois
 * @author Frederic Massart
 */
public class GeoFreeGeoIpImpl implements GeoLocationService {

	private static final Logger LOG = Logger.getLogger(GeoFreeGeoIpImpl.class);
	private static final String FINAL_URL = "http://freegeoip.net/json/";

	@Override
	public GeoLocation requestGeoLocationFromIp(String ip) throws NudgeESConnectorException {
		String geoLocString = getGeoFromIP(ip);
		return parseGeoLocation(geoLocString);
	}

	/**
	 *
	 * @param completeUrl
	 * @return
	 * @throws IOException
	 */
	private HttpURLConnection prepareGeoDataRequest(String completeUrl) throws NudgeESConnectorException {
		try {
			URL url = new URL(completeUrl);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.setConnectTimeout(1000);
			con.setReadTimeout(5000);
			con.setInstanceFollowRedirects(false);
			return con;
		} catch (IOException e) {
			throw new NudgeESConnectorException("Failed to request geo data service", e);
		}
	}

	private GeoLocation parseGeoLocation(String geoLocationString) throws NudgeESConnectorException {
		GeoLocation geoLocation = new GeoLocation();
		if (geoLocationString != null && !geoLocationString.equals("")) {
			ObjectMapper mapper = new ObjectMapper();
			try (MappingIterator<GeoLocation> objectMappingIterator = mapper.reader().forType(GeoLocation.class).readValues(
					geoLocationString)) {
				geoLocation = objectMappingIterator.next();
			} catch (IOException e) {
				throw new NudgeESConnectorException("Failed to parse geo data service response", e);
			}
		}
		return geoLocation;
	}

	/**
	 * Retrieve api data
	 * 
	 * @param userIp
	 * @return
	 * @throws IOException
	 * @throws NudgeESConnectorException
	 */
	private String getGeoFromIP(String userIp) throws NudgeESConnectorException {
		HttpURLConnection connection = prepareGeoDataRequest(FINAL_URL + userIp);
		try {
			LOG.debug(connection.getResponseCode());
			try (InputStream is = connection.getInputStream()) {
				String geoString = convertStreamToString(is);
				connection.disconnect();
				return geoString;
			}
		} catch (IOException e) {
			throw new NudgeESConnectorException("Failed to read geo data service response", e);
		}
	}

	/**
	 * Convert inputStream in String
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

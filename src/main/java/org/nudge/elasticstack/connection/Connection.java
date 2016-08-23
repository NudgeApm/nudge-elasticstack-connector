  package org.nudge.elasticstack.connection;

/**
 * @author : Sarah Bourgeois.
 * @author : Frederic Massart 
 * Description : Connection to the Nudge API. 
 */

import com.nudge.apm.buffer.probe.RawDataProtocol.RawData;
import org.apache.log4j.Logger;
import org.nudge.elasticstack.config.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Connection {
	Configuration config = new Configuration();
	private static final Logger LOG = Logger.getLogger(Connection.class);
	public static final String DATE_FORMAT = "yyyy-MM-dd_HH:mm";
	private final String url;
	private String sessionCookie;

	public Connection(String url) {
		this.url = url;
		sessionCookie = null;
	}

	public void login(String login, String pwd) {
		try {
			HttpURLConnection con;
			URL loginUrl = new URL(url + "login/usrpwd");
			con = (HttpURLConnection) loginUrl.openConnection();
			con.setRequestMethod("POST");
			con.setDoOutput(true);
			con.setConnectTimeout(1000);
			con.setReadTimeout(5000);
			con.setInstanceFollowRedirects(false);
			String params = "id=" + login + "&pwd=" + pwd;
			con.getOutputStream().write(params.getBytes());
			getSessionFromCookies(con.getHeaderFields().get("Set-Cookie"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void getSessionFromCookies(List<String> cookies) {
		if (cookies == null || cookies.size() < 1) {
			throw new IllegalStateException("Login answer does not contains session cookie.");
		}
		for (String cookie : cookies) {
			for (String param : cookie.split(";")) {
				if (param.contains("JSESSIONID=")) {
					sessionCookie = sessionCookie == null ? param : sessionCookie + ";" + param;
				}
				if (param.contains("-cookie=")) {
					sessionCookie = sessionCookie == null ? param : sessionCookie + ";" + param;
				}
			}
		}
	}

	public List<String> requestRawdataList(String appId, String from) throws IOException {
		// TODO parametriser le from
		String finalUrl = url + "api/apps/" + appId + "/rawdata?from=" + config.getRawdataHistory();
		HttpURLConnection connection = prepareRequest(finalUrl);
		LOG.debug(connection.getResponseCode());
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

	public RawData requestRawdata(String appId, String rawdataFilename) throws IOException {
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
			con.setRequestProperty("Cookie", sessionCookie);
			return con;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}

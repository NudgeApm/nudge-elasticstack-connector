package json.connection;

/**
 * Description : Connection to the Nudge API. 
 * @author : Sarah Bourgeois.
 */

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import com.nudge.apm.buffer.probe.RawDataProtocol.RawData;

public class Connection {

	public static final String DATE_FORMAT = "yyyy-MM-dd_HH:mm";
	private final String url;
	private String sessionCookie;

	public Connection(String url) {
		this.url = url;
		sessionCookie = null;
	}

	/**
	 * 
	 * @param login
	 * @param pwd
	 */
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

	private static String convertStreamToString(java.io.InputStream is) {
		try (java.util.Scanner s = new java.util.Scanner(is)) {
			s.useDelimiter("\\A");
			return s.hasNext() ? s.next() : "";
		}
	}

	public List<String> requestRawdataList(String appId, String from) throws IOException {
		List<String> contentRawdata = new ArrayList<String>();
		String finalUrl = "http://dev-nudge.nudge-apm.com:8080/" + "api/apps/" + appId + "/rawdata?from=-10m";
		System.out.println("Request URL for rawdata list : " + finalUrl);
		HttpURLConnection connection = prepareRequest(finalUrl);
		String var = convertStreamToString(connection.getInputStream());
		String var2 = var.substring(1, var.length() - 1);
		String[] var3 = var2.split(",");
		for (String s : var3) {
			String s1 = s.substring(1, s.length() - 1);
			contentRawdata.add(s1);
		}
		connection.disconnect();
		return contentRawdata;
	}

	public RawData requestRawdata(String appId, String rawdataFilename) throws IOException {
		String finalUrl = "http://dev-nudge.nudge-apm.com:8080/" + "api/apps/" + appId + "/rawdata/" + rawdataFilename;
		System.out.println("Request URL for getting a rawdata : " + finalUrl);
		HttpURLConnection connection = prepareRequest(finalUrl);
		System.out.println(connection.getResponseCode());
		return RawData.parseFrom(connection.getInputStream());
	}
}

package json.connection;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import json.bean.TimeSerie;
import json.resolver.TimeSeriesModule;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.Map.Entry;

public class Connection {

	public static final String DEFAULT_URL = "https://monitor.nudge-apm.com/";
	public static final String DATE_FORMAT = "yyyy-MM-dd_HH:mm";
	private final String url;
	private String sessionCookie;
	private boolean logged;

	static ObjectMapper mapper = new ObjectMapper()
			.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
			.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
			.registerModule(new TimeSeriesModule());

	public Connection() {
		this(DEFAULT_URL);
	}

	public Connection(String url) {
		this.url = url;
		logged = false;
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
		logged = true;
	}

	public enum LayerType {
		JAVA, CASSANDRA
	}

	public TimeSerie requestTimeSeries(String appId, Map<String, String> params, LayerType layerType, ObjectMapper mapper) {
		String urlParameter;
		switch (layerType) {
		case JAVA:
			urlParameter = "/metrics/timeSeries";
			break;
		case CASSANDRA:
			urlParameter = "/layers/Cassandra/metrics/timeSeries";
			break;

		default:
			throw new IllegalArgumentException("Invalid Layer type, choose Java or Cassandra for example");
		}

		String data = getData(url + "api/apps/" + appId + urlParameter + paramsToQuery(params));
		return readValue(data, TimeSerie.class, mapper);
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

	private String getData(String completeUrl) {
		if (!logged) {
			throw new IllegalStateException("Can't request while not logged in");
		}
		HttpURLConnection con = prepareRequest(completeUrl);
		try {
			return convertStreamToString(con.getInputStream());
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			con.disconnect();
		}
	}

	private static String paramsToQuery(Map<String, String> params) {
		String query = "";
		for (Entry<String, String> param : params.entrySet()) {
			String paramStr = param.getKey() + "=" + param.getValue();
			query = query.length() == 0 ? paramStr : query + "&" + paramStr;
		}
		return query == null ? "" : "?" + query;
	}

	private static String convertStreamToString(java.io.InputStream is) {
		try (java.util.Scanner s = new java.util.Scanner(is)) {
			s.useDelimiter("\\A");
			return s.hasNext() ? s.next() : "";
		}
	}

	private static <T> T readValue(String content, Class<T> valueType, ObjectMapper mapper) {
		try {
			return mapper.readValue(content, valueType);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public TimeSerie appTimeSerie(String appId, Instant sinceInstant, Instant untilInstant, String step) {
		Map<String, String> params = new HashMap<>();
		params.put("from", formatInstantToNudgeDate(sinceInstant));
		params.put("to", formatInstantToNudgeDate(untilInstant));
		params.put("metrics", "time,count,errors");
		params.put("step", step);

		String data = getData(url + "api/apps/" + appId + "/metrics/timeSeries" + paramsToQuery(params));
		return readValue(data, TimeSerie.class, mapper);
	}

	private String formatInstantToNudgeDate(Instant instant) {
		TimeZone tz = TimeZone.getTimeZone("UTC");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		df.setTimeZone(tz);
		String nowAsISO = df.format(Date.from(instant));
		return nowAsISO;
	}
}

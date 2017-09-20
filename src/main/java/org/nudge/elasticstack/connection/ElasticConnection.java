package org.nudge.elasticstack.connection;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.nudge.elasticstack.context.elasticsearch.ESVersion;
import org.nudge.elasticstack.exception.NudgeESConnectorException;
import org.nudge.elasticstack.exception.UnsupportedElasticStackException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class ElasticConnection {

	private static final Logger LOG = Logger.getLogger(ElasticConnection.class.getName());

	private final String elasticHost;
	private final Metadata metadata;
	private final ESVersion esVersion;
	private String esHostIndexURL;

	public ElasticConnection(String hostUrl) throws Exception {
		this.elasticHost = hostUrl;
		String jsonMetadata = get();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		this.metadata = mapper.readValue(jsonMetadata, Metadata.class);
		this.esVersion = determineESVersion(this.metadata);
	}

	private String get() throws Exception {
		URL url = new URL(elasticHost);
		if (LOG.isDebugEnabled()) {
			LOG.debug("GET " + url);
		}
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.connect();
		String message = readHttpResponse(connection);
		if (connection.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST) {
			return message;
		} else {
			throw new NudgeESConnectorException("Failed ES command with message: " + message);
		}
	}

	public String get(String resource) throws NudgeESConnectorException {
		checkAndLog(HttpMethod.GET, resource, null);
		try {
			URL url = new URL(esHostIndexURL + resource);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.connect();
			String message = readHttpResponse(connection);
			if (connection.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST) {
				return message;
			} else {
				throw new NudgeESConnectorException("Failed ES command with message: " + message);
			}
		} catch (IOException e) {
			throw new NudgeESConnectorException("Failed ES command", e);
		}
	}

	public void put(String resource, String body) throws NudgeESConnectorException {
		checkAndLog(HttpMethod.PUT, resource, body);
		try {
			URL url = new URL(esHostIndexURL + resource);

			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod(HttpMethod.PUT.toString());
			if (body != null) {
				try (OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream())) {
					out.write(body);
					out.close();
				}
			}
			String message = readHttpResponse(connection);
			if (connection.getResponseCode() >= HttpURLConnection.HTTP_BAD_REQUEST) {
				throw new NudgeESConnectorException("Failed ES command with message: " + message);
			}
		} catch (IOException e) {
			throw new NudgeESConnectorException("Failed ES command", e);
		}
	}

	private void checkAndLog(HttpMethod httpMethod, String resource, String body) {
		if (httpMethod == null || resource == null) {
			throw new IllegalArgumentException("Cannot request elasticsearch, HttpMethod and resource URL must be provided");
		}

		if (!LOG.isDebugEnabled()) {
			return;
		}

		String requestLog = httpMethod.toString() + " " + resource;
		if (body != null) {
			requestLog = requestLog + " with body : \n" + body;
		}
		LOG.debug(requestLog);
	}


	public void createAndUseIndex(String esIndex) {
		esHostIndexURL = elasticHost + esIndex + "/";
		try {
			put("", null);
		} catch (NudgeESConnectorException e) {
			LOG.info("Index \"" + esIndex + "\" already exists");
		}
	}

	public ESVersion getEsVersion() {
		return esVersion;
	}

	private ESVersion determineESVersion(Metadata metadata) throws UnsupportedElasticStackException {
		int numberVersion = Integer.parseInt(this.metadata.getVersion().getNumber().split("\\.")[0]);
		switch (numberVersion) {
			case 2:
				return ESVersion.ES2;
			case 5:
				return ESVersion.ES5;
			default:
				throw new UnsupportedElasticStackException(
						"The nudge connector is not compatible with this version of elasticsearch used (" + numberVersion + ".x)");
		}
	}

	private String readHttpResponse(HttpURLConnection connection) throws IOException {
		InputStream respStream = connection.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST ? connection
				.getInputStream() : connection.getErrorStream();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(respStream))) {
			StringBuilder result = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				result.append(line + "\n");
			}
			return result.toString();
		}
	}

	static public class Metadata {
		private Version version;

		public Version getVersion() {
			return version;
		}

		static public class Version {
			private String number;

			public String getNumber() {
				return number;
			}
		}
	}

}

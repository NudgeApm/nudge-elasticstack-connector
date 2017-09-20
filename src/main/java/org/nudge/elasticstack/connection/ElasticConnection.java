package org.nudge.elasticstack.connection;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.nudge.elasticstack.exception.NudgeESConnectorException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class ElasticConnection {

	private static final Logger LOG = Logger.getLogger(ElasticConnection.class.getName());

	private final String elasticHost;
	private final Metadata metadata;
	private final int esVersion;
	private String esCommandPrefix;

	public ElasticConnection(String host) throws Exception {
		this.elasticHost = host;
		String jsonMetadata = get();
		ObjectMapper mapper = new ObjectMapper();
		this.metadata = mapper.readValue(jsonMetadata, Metadata.class);
		this.esVersion = Integer.parseInt(this.metadata.version.number.split("\\.")[0]);
	}

	public String get(String resource) throws NudgeESConnectorException {
		try {
			URL url = new URL(esCommandPrefix + resource);
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
		} catch (IOException e) {
			throw new NudgeESConnectorException("Failed ES command", e);
		}
	}

	public void put(String resource, String body) throws NudgeESConnectorException {
		try {
			URL url = new URL(esCommandPrefix + resource);
			if (LOG.isDebugEnabled()) {
				LOG.debug("PUT " + url);
			}
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("PUT");
			if (body != null) {
				try (OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream())) {
					if (LOG.isDebugEnabled()) {
						LOG.debug("Body : " + body);
					}
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

	private String get() throws Exception {
		URL url = new URL(elasticHost);
		if (LOG.isDebugEnabled()) {
			LOG.debug("Request to : " + url);
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

	public void createAndUseIndex(String esIndex) {
		esCommandPrefix = elasticHost + esIndex + "/";
		try {
			put("", null);
		} catch (NudgeESConnectorException e) {
			LOG.warn("Failed to create index (this error will be ignored, the index could already exists)", e);
		}
	}

	public int getESVersion() {
		return esVersion;
	}

	static public class Metadata {
		private String name;
		@JsonProperty("cluster_name")
		private String clusterName;
		@JsonProperty("cluster_uuid")
		private String clusterUuid;
		private Version version;
		private String tagline;
		
		public String getName() {
			return name;
		}
		public String getVlusterName() {
			return clusterName;
		}
		public String getClusterUuid() {
			return clusterUuid;
		}
		public Version getVersion() {
			return version;
		}
		public String getTagline() {
			return tagline;
		}

		static public class Version {
			private String number;
			@JsonProperty("build_hash")
			private String buildHash;
			@JsonProperty("build_timestamp")
			private String buildTimestamp;
			@JsonProperty("build_date")
			private String buildDate;
			@JsonProperty("build_snapshot")
			private boolean buildSnapshot;
			@JsonProperty("lucene_version")
			private String luceneVersion;
			
			public String getNumber() {
				return number;
			}
			public String getBuildHash() {
				return buildHash;
			}
			public String getBuildTimestamp() {
				return buildTimestamp;
			}
			public String getBuildDate() {
				return buildDate;
			}
			public boolean getBuildSnapshot() {
				return buildSnapshot;
			}
			public String getLuceneVersion() {
				return luceneVersion;
			}
		}
	}

}

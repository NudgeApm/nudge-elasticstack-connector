package org.nudge.elasticstack.connection;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ElasticConnection {

	private static final Logger LOG = Logger.getLogger(ElasticConnection.class.getName());

	private final String elasticHost;
	private final Metadata metadata;
	private final int esVersion;

	public ElasticConnection(String host) throws Exception {
		this.elasticHost = host;
		String jsonMetadata = get("");
		ObjectMapper mapper = new ObjectMapper();
		this.metadata = mapper.readValue(jsonMetadata, Metadata.class);
		this.esVersion = Integer.parseInt(this.metadata.version.number.split("\\.")[0]);
	}

	public String get(String resource) throws Exception {
		URL url = new URL(elasticHost + resource);
		if (LOG.isDebugEnabled()) {
			LOG.debug("Bulk request to : " + url);
		}
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.connect();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
			StringBuilder result = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				result.append(line + "\n");
			}
			return result.toString();
		}
	}

	public void put(String resource, String body) throws Exception {
		URL url = new URL(elasticHost + resource);
		if (LOG.isDebugEnabled()) {
			LOG.debug("Bulk request to : " + url);
		}
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setDoOutput(true);
		connection.setRequestMethod("PUT");
		try (OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream())) {
			out.write(body);
			out.close();
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

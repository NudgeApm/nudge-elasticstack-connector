package mapping;

import org.nudge.elasticstack.NudgeESConnectorException;
import org.nudge.elasticstack.connection.ElasticConnection;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author : Sarah Bourgeois
 * @author : Thomas Arnaud
 *
 */
public class Mapping {
	private final String esCommandPrefix;
	private final ElasticConnection esCon;

	public Mapping(ElasticConnection esCon, String esHost, String esIndex) {
		this.esCon = esCon;
		esCommandPrefix = esHost + esIndex;
	}

	public void pushMappings() throws NudgeESConnectorException {
		// Transaction update mapping
		esCon.put("transaction/_mapping", "{\"properties\":{\"transaction_name\":{\"type\":\"multi_field\",\"fields\":{\"raw\":{\"type\":\"string\",\"index\":\"not_analyzed\"},"
				+ "\"transaction_name\":{\"type\":\"string\",\"index\":\"analyzed\"}}}}}");
		// Sql update mapping
		esCon.put("sql/_mapping", "{\"properties\":{\"sql_code\":{\"type\":\"multi_field\",\"fields\":{\"raw\":{\"type\":\"string\",\"index\":\"not_analyzed\"},"
				+ "\"sql_code\":{\"type\":\"string\",\"index\":\"analyzed\"}}}}}");
		// MBean update mapping
		esCon.put("mbean/_mapping", "{\"properties\":{\"mbean_attributename\":{\"type\":\"multi_field\",\"fields\":{\"raw\":{\"type\":\"string\",\"index\":\"not_analyzed\"},"
				+ "\"mbean_attributename\":{\"type\":\"string\",\"index\":\"analyzed\"}}}}}");
		esCon.put("mbean/_mapping", "{\"properties\":{\"mbean_name\":{\"type\":\"multi_field\",\"fields\":{\"raw\":{\"type\":\"string\",\"index\":\"not_analyzed\"},"
				+ "\"mbean_name\":{\"type\":\"string\",\"index\":\"analyzed\"}}}}}");
		// GeoLocation mapping
		esCon.put("location/_mapping", "{\"properties\":{\"geoPoint\":{\"type\":\"geo_point\",\"geohash\":true,\"geohash_prefix\":true,\"geohash_precision\":7}}}");
	}

	public void initIndex() throws IOException {
		URL url = new URL(esCommandPrefix);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setDoOutput(true);
		con.setRequestMethod("PUT");
		con.getResponseCode();
	}
}

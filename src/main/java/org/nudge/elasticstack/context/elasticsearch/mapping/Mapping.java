package org.nudge.elasticstack.context.elasticsearch.mapping;

import org.nudge.elasticstack.connection.ElasticConnection;
import org.nudge.elasticstack.exception.NudgeESConnectorException;
import org.nudge.elasticstack.exception.UnsupportedElasticStackException;

/**
 * @author : Sarah Bourgeois
 * @author : Thomas Arnaud
 *
 */
public class Mapping {

	private final ElasticConnection esCon;

	public Mapping(ElasticConnection esCon) {
		this.esCon = esCon;
	}

	public void pushMappings() throws NudgeESConnectorException, UnsupportedElasticStackException {
		switch (esCon.getEsVersion()) {
			case ES2:
				pushMappingsES2();
				break;
			case ES5:
				pushMappingsES5();
				break;
		}
	}

	private void pushMappingsES2() throws NudgeESConnectorException {
		// Transaction update org.nudge.elasticstack.context.elasticsearch.mapping
		esCon.put("transaction/_mapping", "{\"properties\":{\"transaction_name\":{\"type\":\"multi_field\",\"fields\":{\"raw\":{\"type\":\"string\",\"index\":\"not_analyzed\"},"
				+ "\"transaction_name\":{\"type\":\"string\",\"index\":\"analyzed\"}}}}}");
		// Sql update org.nudge.elasticstack.context.elasticsearch.mapping
		esCon.put("sql/_mapping", "{\"properties\":{\"sql_code\":{\"type\":\"multi_field\",\"fields\":{\"raw\":{\"type\":\"string\",\"index\":\"not_analyzed\"},"
				+ "\"sql_code\":{\"type\":\"string\",\"index\":\"analyzed\"}}}}}");
		// MBean update org.nudge.elasticstack.context.elasticsearch.mapping
		esCon.put("mbean/_mapping", "{\"properties\":{\"mbean_attributename\":{\"type\":\"multi_field\",\"fields\":{\"raw\":{\"type\":\"string\",\"index\":\"not_analyzed\"},"
				+ "\"mbean_attributename\":{\"type\":\"string\",\"index\":\"analyzed\"}}}}}");
		esCon.put("mbean/_mapping", "{\"properties\":{\"mbean_name\":{\"type\":\"multi_field\",\"fields\":{\"raw\":{\"type\":\"string\",\"index\":\"not_analyzed\"},"
				+ "\"mbean_name\":{\"type\":\"string\",\"index\":\"analyzed\"}}}}}");
		// GeoLocation org.nudge.elasticstack.context.elasticsearch.mapping
		esCon.put("location/_mapping", "{\"properties\":{\"geoPoint\":{\"type\":\"geo_point\",\"geohash\":true,\"geohash_prefix\":true,\"geohash_precision\":7}}}");
	}

	private void pushMappingsES5() throws NudgeESConnectorException {
		// Transaction update org.nudge.elasticstack.context.elasticsearch.mapping
		esCon.put("transaction/_mapping",
				"{" +
						"  \"properties\": {" +
						"    \"transaction_name\": {" +
						"      \"type\": \"text\"," +
						"      \"fields\": {" +
						"        \"keyword\": {" +
						"          \"type\": \"keyword\"" +
						"        }" +
						"      }" +
						"    }" +
						"  }" +
						"}");
		// Sql update org.nudge.elasticstack.context.elasticsearch.mapping
		esCon.put("sql/_mapping",
				"{" +
						"  \"properties\": {" +
						"    \"sql_code\": {" +
						"      \"type\": \"text\"," +
						"      \"fields\": {" +
						"        \"keyword\": {" +
						"          \"type\": \"keyword\"" +
						"        }" +
						"      }" +
						"    }" +
						"  }" +
						"}");
		// MBean update org.nudge.elasticstack.context.elasticsearch.mapping
		esCon.put("mbean/_mapping",
				"{" +
						"  \"properties\": {" +
						"    \"mbean_attributename\": {" +
						"      \"type\": \"text\"," +
						"      \"fields\": {" +
						"        \"keyword\": {" +
						"          \"type\": \"keyword\"" +
						"        }" +
						"      }" +
						"    }" +
						"  }" +
						"}");
		esCon.put("mbean/_mapping",
				"{" +
						"  \"properties\": {" +
						"    \"mbean_name\": {" +
						"      \"type\": \"text\"," +
						"      \"fields\": {" +
						"        \"keyword\": {" +
						"          \"type\": \"keyword\"" +
						"        }" +
						"      }" +
						"    }" +
						"  }" +
						"}");
		// GeoLocation org.nudge.elasticstack.context.elasticsearch.mapping
		esCon.put("location/_mapping",
				"{" +
						"  \"properties\": {" +
						"    \"geoPoint\": {" +
						"      \"type\": \"geo_point\"" +
						"    }" +
						"  }" +
						"}");
	}
}

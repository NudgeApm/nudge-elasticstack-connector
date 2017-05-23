package mapping;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author : Sarah Bourgeois
 *
 */
public class Mapping {

	private static final Logger LOG = Logger.getLogger(Mapping.class.getName());
	private final String esCommandPrefix;

	public enum MappingType {
		TRANSACTION, SQL, MBEAN
	}

	public Mapping(String esHost, String esIndex) {
		esCommandPrefix = esHost + esIndex;
	}

	public void pushMappings() throws IOException {
		// Transaction update mapping
		pushMapping(MappingType.TRANSACTION);
		// Sql update mapping
		pushMapping(MappingType.SQL);
		// MBean update mapping
		pushMapping(MappingType.MBEAN);
		// GeoLocation mapping
		pushGeolocationMapping();
	}

	/**
	 * Update default elasticsearch mapping.
	 *
	 * @param mappingType	the object type concerned by the mapping
	 * @throws IOException 	thrown if a http error occurs
	 */
	public void pushMapping(MappingType mappingType) throws IOException {
		ObjectMapper jsonSerializer = new ObjectMapper();
		MappingProperties mappingProperties = MappingPropertiesBuilder.buildMappingProperties("multi_field", "string",
				"analyzed", "string", "not_analyzed");
		jsonSerializer.enable(SerializationFeature.INDENT_OUTPUT);
		String jsonEvent = jsonSerializer.writeValueAsString(mappingProperties);

		URL url;
		switch (mappingType) {
			case TRANSACTION:
				url = new URL(esCommandPrefix + "/transaction/_mapping");
				HttpURLConnection httpConTrans = (HttpURLConnection) url.openConnection();
				httpConTrans.setDoOutput(true);
				httpConTrans.setRequestMethod("PUT");
				OutputStreamWriter outTrans = new OutputStreamWriter(httpConTrans.getOutputStream());
				outTrans.write(jsonEvent);
				outTrans.close();
				LOG.debug("Transaction Mapping Flushed : " + httpConTrans.getResponseCode() + " - "
						+ httpConTrans.getResponseMessage() + " - " + url.toString());
				break;
			case SQL:
				// change mapping value
				jsonEvent = jsonEvent.replaceAll("transaction_name", "sql_code");
				url = new URL(esCommandPrefix + "/sql/_mapping");
				HttpURLConnection httpConSql = (HttpURLConnection) url.openConnection();
				httpConSql.setDoOutput(true);
				httpConSql.setRequestMethod("PUT");
				OutputStreamWriter outSql = new OutputStreamWriter(httpConSql.getOutputStream());
				outSql.write(jsonEvent);
				outSql.close();
				LOG.debug(
						" SQLLayer Mapping Flushed : " + httpConSql.getResponseCode() + " - " + httpConSql.getResponseMessage());
				break;
			case MBEAN:
				// change mapping value : for attributeName
				jsonEvent = jsonEvent.replaceAll("transaction_name", "mbean_attributename");
				url = new URL(esCommandPrefix + "/mbean/_mapping");
				HttpURLConnection httpConMbean1 = (HttpURLConnection) url.openConnection();
				httpConMbean1.setDoOutput(true);
				httpConMbean1.setRequestMethod("PUT");
				OutputStreamWriter outMbean1 = new OutputStreamWriter(httpConMbean1.getOutputStream());
				outMbean1.write(jsonEvent);
				outMbean1.close();
				LOG.debug(" MBean Mapping 1/2 Flushed : " + httpConMbean1.getResponseCode() + " - "
						+ httpConMbean1.getResponseMessage());
				// change mapping value : for name
				jsonEvent = jsonEvent.replaceAll("mbean_attributename", "mbean_name");
				url = new URL(esCommandPrefix + "/mbean/_mapping");
				HttpURLConnection httpConMbean2 = (HttpURLConnection) url.openConnection();
				httpConMbean2.setDoOutput(true);
				httpConMbean2.setRequestMethod("PUT");
				OutputStreamWriter outMbean2 = new OutputStreamWriter(httpConMbean2.getOutputStream());
				outMbean2.write(jsonEvent);
				outMbean2.close();
				LOG.debug(" MBean Mapping 2/2 Flushed : " + httpConMbean2.getResponseCode() + " - "
						+ httpConMbean2.getResponseMessage());
				break;
		}
	}

	public void pushGeolocationMapping() throws IOException {
		ObjectMapper jsonSerializer = new ObjectMapper();
		MappingPropertiesGeoLocation mpgl = MappingPropertiesGeolocationBuilder
				.buildGeolocationMappingProperties("geo_point", true, true, 7);
		jsonSerializer.enable(SerializationFeature.INDENT_OUTPUT);
		String jsonEvent = jsonSerializer.writeValueAsString(mpgl);
		URL URL = new URL(esCommandPrefix + "/location/_mapping");
		HttpURLConnection httpConTrans = (HttpURLConnection) URL.openConnection();
		httpConTrans.setDoOutput(true);
		httpConTrans.setRequestMethod("PUT");
		OutputStreamWriter outTrans = new OutputStreamWriter(httpConTrans.getOutputStream());
		outTrans.write(jsonEvent);
		outTrans.close();
		LOG.debug(" GeoLocation Mapping Flushed : " + httpConTrans.getResponseCode() + " - "
				+ httpConTrans.getResponseMessage());
	}

	public void initIndex() throws IOException {
		URL url = new URL(esCommandPrefix);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setDoOutput(true);
		con.setRequestMethod("PUT");
		con.getResponseCode();
	}
}

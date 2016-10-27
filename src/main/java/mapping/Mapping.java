package mapping;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.log4j.Logger;
import org.nudge.elasticstack.Configuration;

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

	public enum MappingType {
		TRANSACTION, SQL, MBEAN
	}

	public void pushMapping(Configuration config, MappingType mod) throws IOException {
		pushMapping(config.getOutputElasticHosts(), config.getElasticIndex(), mod);
	}

	public void pushGeolocationMapping(Configuration config) throws IOException {
		pushGeolocationMapping(config.getOutputElasticHosts(), config.getElasticIndex());
	}

	/**
	 * Update default elasticsearch mapping.
	 *
	 * @param elasticURL 	the URL of the ES
	 * @param index			the index to update
	 * @param mappingType	the object type concerned by the mapping
	 * @throws IOException 	thrown if a http error occurs
	 */
	public void pushMapping(String elasticURL, String index, MappingType mappingType) throws IOException {
		ObjectMapper jsonSerializer = new ObjectMapper();
		MappingProperties mappingProperties = MappingPropertiesBuilder.buildMappingProperties("multi_field", "string",
				"analyzed", "string", "not_analyzed");
		jsonSerializer.enable(SerializationFeature.INDENT_OUTPUT);
		String jsonEvent = jsonSerializer.writeValueAsString(mappingProperties);

		URL url;
		switch (mappingType) {
			case TRANSACTION:
				url = new URL(elasticURL + dailyIndex(index) + "/transaction/_mapping");
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
				url = new URL(elasticURL + dailyIndex(index) + "/sql/_mapping");
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
				url = new URL(elasticURL + dailyIndex(index) + "/mbean/_mapping");
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
				url = new URL(elasticURL + dailyIndex(index) + "/mbean/_mapping");
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

	public void pushGeolocationMapping(String elasticURL, String index) throws IOException {
		ObjectMapper jsonSerializer = new ObjectMapper();
		MappingPropertiesGeoLocation mpgl = MappingPropertiesGeolocationBuilder
				.buildGeolocationMappingProperties("geo_point", true, true, 7);
		jsonSerializer.enable(SerializationFeature.INDENT_OUTPUT);
		String jsonEvent = jsonSerializer.writeValueAsString(mpgl);
		URL URL = new URL(elasticURL + dailyIndex(index) + "/location/_mapping");
		HttpURLConnection httpConTrans = (HttpURLConnection) URL.openConnection();
		httpConTrans.setDoOutput(true);
		httpConTrans.setRequestMethod("PUT");
		OutputStreamWriter outTrans = new OutputStreamWriter(httpConTrans.getOutputStream());
		outTrans.write(jsonEvent);
		outTrans.close();
		LOG.debug(" GeoLocation Mapping Flushed : " + httpConTrans.getResponseCode() + " - "
				+ httpConTrans.getResponseMessage());
	}

	// TODO review this with getIndex from BulkFormat
	public static String dailyIndex(String index) {
		String format = "yyyy-MM-dd";
		java.text.SimpleDateFormat formater = new java.text.SimpleDateFormat(format);
		java.util.Date date = new java.util.Date();
		String dateFormater = formater.format(date);
		return index  + "-" + dateFormater;
	}

}

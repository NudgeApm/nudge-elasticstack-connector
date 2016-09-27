package mapping;

/**
 * @author : Sarah Bourgeois
 * 
 */

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import org.apache.log4j.Logger;
import org.nudge.elasticstack.config.Configuration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class Mapping {
	private static final Logger LOG = Logger.getLogger("Update mapping : ");
	Configuration config = new Configuration();
	String elasticURL = config.getOutputElasticHosts();

	public enum MappingType {
		Transaction, Sql, Mbean
	}

	public void pushMapping(Configuration config, Object mod) throws IOException {
		pushMapping(elasticURL, config.getElasticIndex(), mod);
	}

	public void pushGeolocationMapping(Configuration config) throws IOException {
		pushGeolocationMapping(elasticURL, config.getElasticIndex());
	}

	/**
	 * Description : update default elasticsearch mapping
	 * 
	 * @throws IOException
	 */
	public void pushMapping(String elasticURL, String index, Object mod) throws IOException {
		ObjectMapper jsonSerializer = new ObjectMapper();
		MappingProperties mappingProperies = MappingPropertiesBuilder.buildMappingProperties("multi_field", "string",
				"analyzed", "string", "not_analyzed");
		jsonSerializer.enable(SerializationFeature.INDENT_OUTPUT);
		String jsonEvent = jsonSerializer.writeValueAsString(mappingProperies);

		// ***** Case transaction update mapping ******
		if (MappingType.Transaction == mod) {
			URL URL = new URL(elasticURL + index + "/transaction/_mapping");
			HttpURLConnection httpConTrans = (HttpURLConnection) URL.openConnection();
			httpConTrans.setDoOutput(true);
			httpConTrans.setRequestMethod("PUT");
			OutputStreamWriter outTrans = new OutputStreamWriter(httpConTrans.getOutputStream());
			outTrans.write(jsonEvent);
			outTrans.close();
			LOG.debug(" Transaction Mapping Flushed : " + httpConTrans.getResponseCode() + " - "
					+ httpConTrans.getResponseMessage());
		}

		// ******* Case Sql update mapping *******
		if (MappingType.Sql == mod) {
			// change mapping value
			jsonEvent = jsonEvent.replaceAll("transaction_name", "sql_code");
			URL URL = new URL(elasticURL + index + "/sql/_mapping");
			HttpURLConnection httpConSql = (HttpURLConnection) URL.openConnection();
			httpConSql.setDoOutput(true);
			httpConSql.setRequestMethod("PUT");
			OutputStreamWriter outSql = new OutputStreamWriter(httpConSql.getOutputStream());
			outSql.write(jsonEvent);
			outSql.close();
			LOG.debug(
					" Sql Mapping Flushed : " + httpConSql.getResponseCode() + " - " + httpConSql.getResponseMessage());
		}

		// ******* Case Mbean update mapping *******
		if (MappingType.Mbean == mod) {
			// change mapping value : for attributeName
			jsonEvent = jsonEvent.replaceAll("transaction_name", "mbean_attributename");
			URL URL = new URL(elasticURL + index + "/mbean/_mapping");
			HttpURLConnection httpConMbean1 = (HttpURLConnection) URL.openConnection();
			httpConMbean1.setDoOutput(true);
			httpConMbean1.setRequestMethod("PUT");
			OutputStreamWriter outMbean1 = new OutputStreamWriter(httpConMbean1.getOutputStream());
			outMbean1.write(jsonEvent);
			outMbean1.close();
			LOG.debug(" Mbean Mapping 1/2 Flushed : " + httpConMbean1.getResponseCode() + " - "
					+ httpConMbean1.getResponseMessage());
			// change mapping value : for name
			jsonEvent = jsonEvent.replaceAll("mbean_attributename", "mbean_name");
			URL = new URL(elasticURL + index + "/mbean/_mapping");
			HttpURLConnection httpConMbean2 = (HttpURLConnection) URL.openConnection();
			httpConMbean2.setDoOutput(true);
			httpConMbean2.setRequestMethod("PUT");
			OutputStreamWriter outMbean2 = new OutputStreamWriter(httpConMbean2.getOutputStream());
			outMbean2.write(jsonEvent);
			outMbean2.close();
			LOG.debug(" Mbean Mapping 2/2 Flushed : " + httpConMbean2.getResponseCode() + " - "
					+ httpConMbean2.getResponseMessage());
		}
	}

	public void pushGeolocationMapping(String elasticURL, String index) throws IOException {
		ObjectMapper jsonSerializer = new ObjectMapper();
		MappingPropertiesGeoLocation mpgl = MappingPropertiesGeolocationBuilder
				.buildGeolocationMappingProperties("geo_point", true, true, 7);
		jsonSerializer.enable(SerializationFeature.INDENT_OUTPUT);
		String jsonEvent = jsonSerializer.writeValueAsString(mpgl);
		URL URL = new URL(elasticURL + index + "/location/_mapping");
		HttpURLConnection httpConTrans = (HttpURLConnection) URL.openConnection();
		httpConTrans.setDoOutput(true);
		httpConTrans.setRequestMethod("PUT");
		OutputStreamWriter outTrans = new OutputStreamWriter(httpConTrans.getOutputStream());
		outTrans.write(jsonEvent);
		outTrans.close();
		LOG.debug(" GeoLocation Mapping Flushed : " + httpConTrans.getResponseCode() + " - "
				+ httpConTrans.getResponseMessage());
	}

}

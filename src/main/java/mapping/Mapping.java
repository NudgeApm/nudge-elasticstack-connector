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
import org.nudge.elasticstack.json.bean.MappingProperties;
import org.nudge.elasticstack.json.bean.MappingPropertiesBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class Mapping {

	private static final Logger LOG = Logger.getLogger("Update mapping : ");
	public static final int typeSql = 2;
	public static final int typeTransaction = 1;
	public static final int typeMbean = 3;

	/**
	 * 
	 * @param config
	 * @param mod
	 * @throws IOException
	 */
	public void pushMapping(Configuration config, int mod) throws IOException {
		String elasticURL = config.getOutputElasticHosts();
		pushMapping(elasticURL, config.getElasticIndex(), mod);
	}

	/**
	 * Description : update default elasticsearch mapping
	 * 
	 * @throws IOException
	 */
	public void pushMapping(String elasticURL, String index, int mod) throws IOException {
		ObjectMapper jsonSerializer = new ObjectMapper();
		MappingProperties mappingProperies = MappingPropertiesBuilder.buildMappingProperties("multi_field", "string",
				"analyzed", "string", "not_analyzed");
		jsonSerializer.enable(SerializationFeature.INDENT_OUTPUT);
		String jsonEvent = jsonSerializer.writeValueAsString(mappingProperies);

		switch (mod) {
		// ***** Case transaction update mapping ******
		case typeTransaction:
			URL URL = new URL(elasticURL + index + "/transaction/_mapping");
			HttpURLConnection httpConSql = (HttpURLConnection) URL.openConnection();
			httpConSql.setDoOutput(true);
			httpConSql.setRequestMethod("PUT");
			OutputStreamWriter outT = new OutputStreamWriter(httpConSql.getOutputStream());
			outT.write(jsonEvent);
			outT.close();
			LOG.debug(" Transaction Mapping Flushed : " + httpConSql.getResponseCode() + " - "
					+ httpConSql.getResponseMessage());
			break;

		// ******* Case Sql update mapping *******
		case typeSql:
			// change mapping value
			jsonEvent = jsonEvent.replaceAll("name", "codeSql");
			URL = new URL(elasticURL + index + "/sql/_mapping");
			HttpURLConnection httpCon2 = (HttpURLConnection) URL.openConnection();
			httpCon2.setDoOutput(true);
			httpCon2.setRequestMethod("PUT");
			OutputStreamWriter out = new OutputStreamWriter(httpCon2.getOutputStream());
			out.write(jsonEvent);
			
			out.close();
			LOG.debug(" Sql Mapping Flushed : " + httpCon2.getResponseCode() + " - " + httpCon2.getResponseMessage());
			break;

		// ******* Case Mbean update mapping *******
		case typeMbean:
			// change mapping value : for NameMbean
			jsonEvent = jsonEvent.replaceAll("name", "nameMbean");
			URL = new URL(elasticURL + index + "/mbean/_mapping");
			HttpURLConnection httpCon3 = (HttpURLConnection) URL.openConnection();
			httpCon3.setDoOutput(true);
			httpCon3.setRequestMethod("PUT");
			OutputStreamWriter out3 = new OutputStreamWriter(httpCon3.getOutputStream());
			out3.write(jsonEvent);
			out3.close();
			LOG.debug(" Sql Mapping Flushed : " + httpCon3.getResponseCode() + " - " + httpCon3.getResponseMessage());
			
			// change mapping value : for ObjectMbean
			jsonEvent = jsonEvent.replaceAll("name", "objectName");
			URL = new URL(elasticURL + index + "/mbean/_mapping");
			HttpURLConnection httpCon4 = (HttpURLConnection) URL.openConnection();
			httpCon4.setDoOutput(true);
			httpCon4.setRequestMethod("PUT");
			OutputStreamWriter out4 = new OutputStreamWriter(httpCon4.getOutputStream());
			out4.write(jsonEvent);
			out4.close();
			LOG.debug(" Sql Mapping Flushed : " + httpCon4.getResponseCode() + " - " + httpCon4.getResponseMessage());
			
			
			break;

		} // end switch
	}

}

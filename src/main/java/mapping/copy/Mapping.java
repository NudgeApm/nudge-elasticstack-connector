package mapping.copy;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.log4j.Logger;
import org.nudge.elasticstack.Daemon;
import org.nudge.elasticstack.config.Configuration;
import org.nudge.elasticstack.json.bean.MappingProperties;
import org.nudge.elasticstack.json.bean.MappingPropertiesBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class Mapping {
	
	private static final Logger LOG = Logger.getLogger(Daemon.class);

	public static void pushMapping(Configuration config) throws IOException {
		String elasticURL = config.getOutputElasticHosts();
		pushMapping(elasticURL, config.getElasticIndex());

	}

	/**
	 * Description : update default elasticsearch mapping
	 *
	 * @throws IOException
	 */
	public static void pushMapping(String elasticURL, String index) throws IOException {
		ObjectMapper jsonSerializer = new ObjectMapper();
		MappingProperties mappingProperies = MappingPropertiesBuilder.buildMappingProperties("multi_field",
				"string", "analyzed", "string", "not_analyzed");
		jsonSerializer.enable(SerializationFeature.INDENT_OUTPUT);
		String jsonEvent = jsonSerializer.writeValueAsString(mappingProperies);
		URL URL = new URL(elasticURL + index + "/transaction/_mapping");
		System.out.println(URL);
		System.out.println("     ");
		System.out.println("      ");
		System.out.println(URL);

		HttpURLConnection httpCon2 = (HttpURLConnection) URL.openConnection();
		httpCon2.setDoOutput(true);
		httpCon2.setRequestMethod("PUT");
		OutputStreamWriter out = new OutputStreamWriter(httpCon2.getOutputStream());

		out.write(jsonEvent);
		out.close();
		LOG.info(" Transaction Mapping Flushed : " + httpCon2.getResponseCode() + " - "
				+ httpCon2.getResponseMessage());
	}
	
	/**
	 * Description : update default elasticsearch mapping
	 *
	 * @throws IOException
	 */
	public static void pushMappingSql(String elasticURL, String index) throws IOException {
		ObjectMapper jsonSerializer = new ObjectMapper();
		MappingProperties mappingProperies = MappingPropertiesBuilder.buildMappingProperties("multi_field",
				"string", "analyzed", "string", "not_analyzed");
		jsonSerializer.enable(SerializationFeature.INDENT_OUTPUT);
		String jsonEvent = jsonSerializer.writeValueAsString(mappingProperies);
		URL URL = new URL(elasticURL + index + "/SQL/_mapping");
		System.out.println(URL);
		System.out.println("     ");
		System.out.println("      ");
		System.out.println(URL);

		HttpURLConnection httpCon2 = (HttpURLConnection) URL.openConnection();
		httpCon2.setDoOutput(true);
		httpCon2.setRequestMethod("PUT");
		OutputStreamWriter out = new OutputStreamWriter(httpCon2.getOutputStream());

		out.write(jsonEvent);
		out.close();
		LOG.info(" Transaction Mapping Flushed : " + httpCon2.getResponseCode() + " - "
				+ httpCon2.getResponseMessage());
	}
}

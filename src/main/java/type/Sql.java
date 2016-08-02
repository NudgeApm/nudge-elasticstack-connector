package type;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.nudge.elasticstack.BulkFormat;
import org.nudge.elasticstack.config.Configuration;
import org.nudge.elasticstack.json.bean.EventSQL;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.nudge.apm.buffer.probe.RawDataProtocol.Layer;
import com.nudge.apm.buffer.probe.RawDataProtocol.LayerDetail;
import com.nudge.apm.buffer.probe.RawDataProtocol.Transaction;

public class Sql {

	private static final Logger LOG = Logger.getLogger(Sql.class);
	private static final String lineBreak = "\n";
	Configuration config = new Configuration();

	/**
	 * Description : retrieve SQL request
	 */
	public List<EventSQL> buildSqlEvents(List<Transaction> transaction) {
		List<EventSQL> eventSqls = new ArrayList<>();
		List<Layer> layer = new ArrayList<>();
		List<LayerDetail> layerDetail = new ArrayList<>();
		for (Transaction trans : transaction) {
			trans.getLayersList();
			layer.addAll(trans.getLayersList());
		}
		for (Layer lay : layer) {
			lay.getCallsList();
			layerDetail.addAll(lay.getCallsList());
		}
		for (LayerDetail layd : layerDetail) {
			String codeSql = null, timestampSql;
			long countSql = 0, timeSql = 0;
			codeSql = layd.getCode();
			countSql = layd.getCount();
			timeSql = layd.getTime();
			SimpleDateFormat sdfr = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
			timestampSql = sdfr.format(layd.getTimestamp());
			EventSQL sqlevent = new EventSQL(timestampSql, codeSql, countSql, timeSql);
			eventSqls.add(sqlevent);
		}
		return eventSqls;
	}

	/**
	 * Description : Parse SQL to send to Elastic
	 *
	 * @param eventList
	 * @return
	 * @throws Exception
	 */
	public List<String> parseJsonSQL(List<EventSQL> eventSqls) throws Exception {
		List<String> jsonEventsSql = new ArrayList<String>();
		ObjectMapper jsonSerializer = new ObjectMapper();

		if (config.getDryRun()) {
			jsonSerializer.enable(SerializationFeature.INDENT_OUTPUT);
		}
		for (EventSQL event : eventSqls) {
			String jsonMetadata = generateMetaDataSQL(event.getCodeSql());
			jsonEventsSql.add(jsonMetadata + lineBreak);
			// Handle data event
			String jsonEvent = jsonSerializer.writeValueAsString(event);
			jsonEventsSql.add(jsonEvent + lineBreak);
		}
		LOG.debug(jsonEventsSql);
		return jsonEventsSql;
	}

	/**
	 * Description : generate SQL for Bulk api
	 *
	 * @param mbean
	 * @return
	 * @throws JsonProcessingException
	 */
	public String generateMetaDataSQL(String sql) throws JsonProcessingException {
		Configuration conf = new Configuration();
		ObjectMapper jsonSerializer = new ObjectMapper();
		if (config.getDryRun()) {
			jsonSerializer.enable(SerializationFeature.INDENT_OUTPUT);
		}
		BulkFormat elasticMetaData = new BulkFormat();
		elasticMetaData.getIndexElement().setIndex(conf.getElasticIndex());
		elasticMetaData.getIndexElement().setId(UUID.randomUUID().toString());
		elasticMetaData.getIndexElement().setType("sql");
		return jsonSerializer.writeValueAsString(elasticMetaData);
	}

	/**
	 * Description : Send MBean into elasticSearch
	 *
	 * @param jsonEvents2
	 * @throws IOException
	 */
	public void sendSqltoElk(List<String> jsonEventsSql) throws IOException {
		Configuration conf = new Configuration();
		StringBuilder sb = new StringBuilder();
		for (String json : jsonEventsSql) {
			sb.append(json);
		}
		if (config.getDryRun()) {
			LOG.info("Dry run active, only log documents, don't push to elasticsearch.");
			return;
		}
		URL URL = new URL(conf.getOutputElasticHosts() + "_bulk");
		if (LOG.isDebugEnabled()) {
			LOG.debug("Bulk request to : " + URL);
		}
		HttpURLConnection httpCon2 = (HttpURLConnection) URL.openConnection();
		httpCon2.setDoOutput(true);
		httpCon2.setRequestMethod("PUT");
		OutputStreamWriter out = new OutputStreamWriter(httpCon2.getOutputStream());
		if (sb.length() == 0) {
			return;
		} else {
			out.write(sb.toString());
		}
		out.close();
		LOG.info(" Sending Sql : " + httpCon2.getResponseCode() + " - " + httpCon2.getResponseMessage());
	}

} // End of class

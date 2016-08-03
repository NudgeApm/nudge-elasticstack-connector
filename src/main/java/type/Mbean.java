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
import org.nudge.elasticstack.json.bean.EventMBean;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.nudge.apm.buffer.probe.RawDataProtocol.Dictionary;
import com.nudge.apm.buffer.probe.RawDataProtocol.MBean;
import com.nudge.apm.buffer.probe.RawDataProtocol.MBeanAttributeInfo;
import com.nudge.apm.buffer.probe.RawDataProtocol.Dictionary.DictionaryEntry;

public class Mbean {

	private static final Logger LOG = Logger.getLogger("Mbean type : ");
	private static final String lineBreak = "\n";
	Configuration config = new Configuration();

	/**
	 * Description : retrieve Mbean from rawdata
	 *
	 * @param mbean
	 * @param dictionary
	 * @return
	 * @throws JsonProcessingException
	 */
	public List<EventMBean> buildMbeanEvents(List<MBean> mbean, Dictionary dictionary) throws JsonProcessingException {
		List<EventMBean> eventsMbean = new ArrayList<EventMBean>();
		List<DictionaryEntry> dico = dictionary.getDictionaryList();

		// retrieve MBean
		for (MBean mb : mbean) {
			for (MBeanAttributeInfo mBeanAttributeInfo : mb.getAttributeInfoList()) {
				String nameMbean = null, objectName = null, type = null, valueMbean = null;
				int countAttribute = 0, nameId = 0, typeId = 0;
				String collectingTime;
				
				SimpleDateFormat sdfr = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
				collectingTime = sdfr.format(mb.getCollectingTime());
				objectName = mb.getObjectName();
				countAttribute = mb.getAttributeInfoCount();
				nameId = mBeanAttributeInfo.getNameId();
				type = "Mbean";
				typeId = mBeanAttributeInfo.getTypeId();
				valueMbean = mBeanAttributeInfo.getValue();
				EventMBean mbeanEvent = new EventMBean(nameMbean, objectName, type, typeId, nameId, valueMbean,
						collectingTime, countAttribute);
				// retrieve nameMbean with Dictionary
				for (DictionaryEntry dictionaryEntry : dico) {
					String name = dictionaryEntry.getName();
					int id = dictionaryEntry.getId();
					if (nameId == id) {
						nameMbean = mbeanEvent.setNameMbean(name);
					}
				}
				// add events
				eventsMbean.add(mbeanEvent);
			}
		}
		return eventsMbean;
	}

	/**
	 * Description : Parse Mbean to send to Elastic
	 *
	 * @param eventList
	 * @return
	 * @throws Exception
	 */
	public List<String> parseJsonMBean(List<EventMBean> eventList) throws Exception {
		List<String> jsonEvents2 = new ArrayList<String>();
		ObjectMapper jsonSerializer = new ObjectMapper();

		if (config.getDryRun()) {
			jsonSerializer.enable(SerializationFeature.INDENT_OUTPUT);
		}

		for (EventMBean event : eventList) {
			String jsonMetadata = generateMetaDataMbean(event.getType());
			jsonEvents2.add(jsonMetadata + lineBreak);
			// Handle data event
			String jsonEvent = jsonSerializer.writeValueAsString(event);
			jsonEvents2.add(jsonEvent + lineBreak);
		}
		LOG.debug(jsonEvents2);
		return jsonEvents2;
	}

	/**
	 * Description : generate Mbean for Bulk api
	 *
	 * @param mbean
	 * @return
	 * @throws JsonProcessingException
	 */
	public String generateMetaDataMbean(String mbean) throws JsonProcessingException {
		Configuration conf = new Configuration();
		ObjectMapper jsonSerializer = new ObjectMapper();
		if (config.getDryRun()) {
			jsonSerializer.enable(SerializationFeature.INDENT_OUTPUT);
		}
		BulkFormat elasticMetaData = new BulkFormat();
		elasticMetaData.getIndexElement().setIndex(conf.getElasticIndex());
		elasticMetaData.getIndexElement().setId(UUID.randomUUID().toString());
		elasticMetaData.getIndexElement().setType("mbean");
		return jsonSerializer.writeValueAsString(elasticMetaData);
	}

	/**
	 * Description : Send MBean into elasticSearch
	 *
	 * @param jsonEvents2
	 * @throws IOException
	 */
	public void sendElk(List<String> jsonEvents2) throws IOException {
		Configuration conf = new Configuration();
		StringBuilder sb = new StringBuilder();

		for (String json : jsonEvents2) {
			sb.append(json);
		}
		if (config.getDryRun()) {
			LOG.info("Dry run active, only log documents, don't push to elasticsearch.");
			return;
		}
		long start = System.currentTimeMillis();
		URL URL = new URL(conf.getOutputElasticHosts() + "_bulk");
		if (LOG.isDebugEnabled()) {
			LOG.debug("Bulk request to : " + URL);
		}
		HttpURLConnection httpCon2 = (HttpURLConnection) URL.openConnection();
		httpCon2.setDoOutput(true);
		httpCon2.setRequestMethod("PUT");
		OutputStreamWriter out = new OutputStreamWriter(httpCon2.getOutputStream());
		out.write(sb.toString());
		out.close();
		long end = System.currentTimeMillis();
		long totalTime = end - start;
		LOG.info(" Flush " + jsonEvents2.size() + " documents insert in BULK in : " + (totalTime / 1000f) + "sec");
		LOG.debug(" Sending Mbean : " + httpCon2.getResponseCode() + " - " + httpCon2.getResponseMessage());
	}

} // End of class

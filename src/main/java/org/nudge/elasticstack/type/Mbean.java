package org.nudge.elasticstack.type;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.nudge.apm.buffer.probe.RawDataProtocol.Dictionary;
import com.nudge.apm.buffer.probe.RawDataProtocol.Dictionary.DictionaryEntry;
import org.apache.log4j.Logger;
import org.nudge.elasticstack.BulkFormat;
import org.nudge.elasticstack.bean.rawdata.MBeanFred;
import org.nudge.elasticstack.config.Configuration;
import org.nudge.elasticstack.json.bean.EventMBean;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class Mbean {

	private static final Logger LOG = Logger.getLogger("Mbean org.nudge.elasticstack.type : ");
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
	public List<EventMBean> buildMbeanEvents(List<MBeanFred> mbean, Dictionary dictionary) throws JsonProcessingException {
		List<EventMBean> eventsMbean = new ArrayList<EventMBean>();
		List<DictionaryEntry> dico = dictionary.getDictionaryList();

		// TODO FMA extract SDF to builder
		SimpleDateFormat sdfr = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		// retrieve MBean
		for (MBeanFred mb : mbean) {

			String collectingTime = sdfr.format(mb.getCollectingTime());
			String objectName = mb.getObjectName();
			int countAttribute = mb.getAttributeInfoCount();

			for (MBeanFred.AttributeInfo mBeanAttributeInfo : mb.getAttributeInfos()) {
				String nameMbean = null;
				double valueMbean = 0;
			int	nameId = mBeanAttributeInfo.getNameId();
			String	type = "Mbean";
				try {
					valueMbean = Double.parseDouble(mBeanAttributeInfo.getValue());
				} catch (NumberFormatException nfe) { 
					if (LOG.isDebugEnabled()) {
						LOG.debug("Impossible to get the value of a mbean, it will not be inserted to ELK. MBean from rawdata : " + mBeanAttributeInfo);
					}
				}
				EventMBean mbeanEvent = new EventMBean(nameMbean, objectName, type, valueMbean,
						collectingTime, countAttribute, EventMBean.getTransactionId());
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

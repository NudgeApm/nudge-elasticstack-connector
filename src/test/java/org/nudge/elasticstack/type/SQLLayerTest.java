package org.nudge.elasticstack.type;

import org.junit.Assert;
import org.junit.Test;
import org.nudge.elasticstack.context.elasticsearch.bean.SQLEvent;
import org.nudge.elasticstack.context.elasticsearch.builder.LayerTransformer;
import org.nudge.elasticstack.context.nudge.dto.LayerCallDTO;
import org.nudge.elasticstack.context.nudge.dto.LayerDTO;
import org.nudge.elasticstack.context.nudge.dto.TransactionDTO;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Test class of {@link LayerTransformer}
 */
public class SQLLayerTest {

	private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

	private LayerTransformer layerTransformer = new LayerTransformer();

	@Test
	public void test_BuildSQLEvents() {
		String appId = "1234q";
		String appName = "app-name";
		String host = "windows1234";
		String hostname = "my-wonderful-pc";
		String sqlReq = "SELECT * FROM users;";
		long responseTime = 1500;
		int count = 1;
		Date date = new Date();

		List<SQLEvent> sqlEvents = layerTransformer.buildSQLEvents(appId, appName,host, hostname, Collections.singletonList(
				buildTransactionDTO(sqlReq, responseTime, 1, date.getTime())));

		for (SQLEvent sqlEvent : sqlEvents) {
			Assert.assertEquals(appId, sqlEvent.getAppId());
			Assert.assertEquals(appName, sqlEvent.getAppName());
			Assert.assertEquals(host, sqlEvent.getHost());
			Assert.assertEquals(hostname, sqlEvent.getHostname());

			Assert.assertEquals(sqlReq, sqlEvent.getName());
			Assert.assertEquals(responseTime, sqlEvent.getResponseTime());
			Assert.assertEquals(count, sqlEvent.getCount());
			Assert.assertEquals(SDF.format(date), sqlEvent.getDate());
		}
	}


	private TransactionDTO buildTransactionDTO(String sqlReq, long responseTime, int count, long time) {
		TransactionDTO transactionDTO = new TransactionDTO();
		LayerDTO layer = transactionDTO.addNewLayerDTO();
		LayerCallDTO layerCall = layer.createAddLayerDetail();
		layer.setLayerName("SQL");
		layer.setCount(count);
		layer.setTime(responseTime);

		layerCall.setCount(count);
		layerCall.setCode(sqlReq);
		layerCall.setResponseTime(responseTime);
		layerCall.setTimestamp(time);

		// add the layerCall a second time
		layer.addLayerDetail(layerCall);
		return transactionDTO;
	}

}
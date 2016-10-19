package org.nudge.elasticstack.type;

import org.junit.Assert;
import org.junit.Test;
import org.nudge.elasticstack.bean.rawdata.LayerDTO;
import org.nudge.elasticstack.bean.rawdata.TransactionDTO;
import org.nudge.elasticstack.json.bean.EventSQL;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Test class of {@link SQLLayer}
 */
public class SQLLayerTest {

	private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

	private SQLLayer sqlLayer = new SQLLayer();

	@Test
	public void test_BuildSQLEvents() {
		String sqlReq = "SELECT * FROM users;";
		long responseTime = 1500;
		int count = 1;
		Date date = new Date();

		List<EventSQL> eventSQLs = sqlLayer.buildSQLEvents(Collections.singletonList(
				buildTransactionDTO(sqlReq, responseTime, 1, date.getTime())));

		for (EventSQL eventSQL : eventSQLs) {
			Assert.assertEquals(sqlReq, eventSQL.getName());
			Assert.assertEquals(responseTime, eventSQL.getResponseTime());
			Assert.assertEquals(count, eventSQL.getCount());
			Assert.assertEquals(SDF.format(date), eventSQL.getDate());
		}
	}


	private TransactionDTO buildTransactionDTO(String sqlReq, long responseTime, int count, long time) {
		TransactionDTO transactionDTO = new TransactionDTO();
		LayerDTO layer = transactionDTO.addNewLayerDTO();
		LayerDTO.LayerDetail layerDetail = layer.createAddLayerDetail();
		layer.setLayerName("SQL");
		layer.setCount(count);
		layer.setTime(responseTime);

		layerDetail.setCount(count);
		layerDetail.setCode(sqlReq);
		layerDetail.setResponseTime(responseTime);
		layerDetail.setTimestamp(time);

		// add the layerDetail a second time
		layer.addLayerDetail(layerDetail);
		return transactionDTO;
	}

}
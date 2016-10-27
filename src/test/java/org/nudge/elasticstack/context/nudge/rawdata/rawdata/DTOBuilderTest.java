package org.nudge.elasticstack.context.nudge.rawdata.rawdata;

import com.nudge.apm.buffer.probe.RawDataProtocol;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.nudge.elasticstack.context.nudge.dto.DTOBuilder;
import org.nudge.elasticstack.context.nudge.dto.LayerCallDTO;
import org.nudge.elasticstack.context.nudge.dto.LayerDTO;
import org.nudge.elasticstack.context.nudge.dto.MBeanDTO;
import org.nudge.elasticstack.context.nudge.dto.TransactionDTO;
import org.nudge.elasticstack.context.nudge.filter.bean.Filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Test class for {@link DTOBuilder}
 */
public class DTOBuilderTest {

	private RawDataProtocol.RawData rawData;

	/**
	 * Prepare the test by reading a sample example of a Nudge APM rawdata.
	 */
	@Before
	public void init() throws IOException {
		this.rawData = readRawdata();
	}

	public RawDataProtocol.RawData readRawdata() throws IOException {
		try {
			return RawDataProtocol.RawData.parseFrom(this.getClass().getClassLoader()
					.getResourceAsStream("rawdata/collecte_2016-09-29_10-54-01-620_140.dat"));
		} catch (IOException e) {
			throw new IOException("Impossible to read the sample rawdata", e);

		}
	}

	@Test
	public void buildTransactions() throws Exception {
		List<Filter> filters = new ArrayList<>();
		List<TransactionDTO> transactionDTOList = DTOBuilder.buildTransactions(rawData.getTransactionsList(), filters);

		// First test : transaction stuff
		RawDataProtocol.Transaction expectedTrans = rawData.getTransactionsList().get(0);
		TransactionDTO transaction = transactionDTOList.get(0);
		Assert.assertEquals(expectedTrans.getCode(), transaction.getCode());
		Assert.assertEquals(expectedTrans.getStartTime(), transaction.getStartTime());
		Assert.assertEquals(expectedTrans.getEndTime(), transaction.getEndTime());
		Assert.assertEquals(expectedTrans.getUserIp(), transaction.getUserIp());
		Assert.assertEquals(expectedTrans.getLayersList().size(), transaction.getLayers().size());

		// second test : layer stuff belongs to a transaction
		RawDataProtocol.Layer expectedLayer = expectedTrans.getLayersList().get(0);
		LayerDTO layer = transaction.getLayers().get(0);
		Assert.assertEquals(expectedLayer.getLayerName(), layer.getLayerName());
		Assert.assertEquals(expectedLayer.getTime(), layer.getTime());
		Assert.assertEquals(expectedLayer.getCount(), layer.getCount());

		// third test : layer detail stuff belongs to a layer
		RawDataProtocol.LayerDetail expectedLayerDetail = expectedLayer.getCallsList().get(0);
		LayerCallDTO layerDetails = layer.getCalls().get(0);
		Assert.assertEquals(expectedLayerDetail.getTimestamp(), layerDetails.getTimestamp());
		Assert.assertEquals(expectedLayerDetail.getCode(), layerDetails.getCode());
		Assert.assertEquals(expectedLayerDetail.getCount(), layerDetails.getCount());
		Assert.assertEquals(expectedLayerDetail.getTime(), layerDetails.getResponseTime());
	}

	@Test
	public void buildMBeans() throws Exception {
		List<MBeanDTO> mbeanDTOList = DTOBuilder.buildMBeans(rawData.getMBeanList());

		RawDataProtocol.MBean expectedMbean = rawData.getMBeanList().get(0);
		MBeanDTO mbean = mbeanDTOList.get(0);
		Assert.assertEquals(expectedMbean.getAttributeInfoCount(), mbean.getAttributeInfoCount());
		Assert.assertEquals(expectedMbean.getCollectingTime(), mbean.getCollectingTime());
		Assert.assertEquals(expectedMbean.getObjectName(), mbean.getObjectName());

		RawDataProtocol.MBeanAttributeInfo expectedAttributeInfo = expectedMbean.getAttributeInfoList().get(0);
		MBeanDTO.AttributeInfo attributeInfo = mbean.getAttributeInfos().get(0);
		Assert.assertEquals(expectedAttributeInfo.getNameId(), attributeInfo.getNameId());
		Assert.assertEquals(expectedAttributeInfo.getValue(), attributeInfo.getValue());
	}

}
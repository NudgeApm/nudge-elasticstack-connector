package org.nudge.elasticstack.type;

import com.nudge.apm.buffer.probe.RawDataProtocol;
import org.junit.Assert;
import org.junit.Test;
import org.nudge.elasticstack.Utils;
import org.nudge.elasticstack.context.elasticsearch.json.EventMBean;
import org.nudge.elasticstack.context.nudge.dto.MBeanDTO;
import org.nudge.elasticstack.context.nudge.rawdata.rawdata.DTOBuilderTest;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Test class for the {@link MBean} class.
 */
public class MBeanTest {

	MBean mBean = new MBean();

	@Test
	public void buildMbeanEvents() throws Exception {
		DTOBuilderTest dto = new DTOBuilderTest();
		RawDataProtocol.RawData rawData = dto.readRawdata();
		List<EventMBean> eventMBean = mBean.buildMbeanEvents(Collections.singletonList(buildSampleMBeanDTO()), rawData.getMbeanDictionary());

		Assert.assertEquals(2, eventMBean.size());
		Assert.assertEquals("TransactionsQueueSize", eventMBean.get(0).getNameMbean());
		Assert.assertEquals(2D, eventMBean.get(0).getValueMbean(), 0);
		Assert.assertEquals(0D, eventMBean.get(1).getValueMbean(), 0);
		Assert.assertEquals(Utils.ES_DATE_FORMAT.format(new Date(1475139154420L)), eventMBean.get(0).getCollectingTime());
	}

//	*** Utility methods ***

	private MBeanDTO buildSampleMBeanDTO() {
		MBeanDTO mBeanDTO = new MBeanDTO();
		mBeanDTO.setObjectName("org.nudge:type=Collector");
		mBeanDTO.setCollectingTime(1475139154420L);

		buildAttributeInfo(mBeanDTO, 0, "2"); // TransactionsQueueSize
		buildAttributeInfo(mBeanDTO, 2, "0"); // JmxSamplesSize

		return mBeanDTO;
	}

	private MBeanDTO.AttributeInfo buildAttributeInfo(MBeanDTO mBeanDTO, int nameId, String value) {
		MBeanDTO.AttributeInfo attributeInfo = mBeanDTO.addNewAttributeInfo();
		attributeInfo.setNameId(nameId);
		attributeInfo.setValue(value);
		return attributeInfo;
	}
}
package org.nudge.elasticstack.bean.rawdata;

import com.nudge.apm.buffer.probe.RawDataProtocol;
import com.nudge.apm.buffer.probe.RawDataProtocol.Layer;
import java.util.ArrayList;
import java.util.List;

/**
 * Mapping builder for Nudge APM rawdata objects.
 *
 * @author Sarah Bourgeois
 * @author Frederic Massart
 */
public class FredBuilder {

	public static List<TransactionFred> buildTransactions(List<RawDataProtocol.Transaction> rawdataTransactions) {
		List<TransactionFred> transactions = new ArrayList<>(rawdataTransactions.size());

		for (RawDataProtocol.Transaction rawdataTransaction : rawdataTransactions) {
			TransactionFred transaction = new TransactionFred();
			transaction.setCode(rawdataTransaction.getCode());
			transaction.setStartTime(rawdataTransaction.getStartTime());
			transaction.setEndTime(rawdataTransaction.getEndTime());
			transaction.setUserIp(rawdataTransaction.getUserIp());
			
			List<LayerFred> layers = new ArrayList<>();
			for (Layer lay : rawdataTransaction.getLayersList()) {
				LayerFred layer = new LayerFred();
				layer.setLayerName(lay.getLayerName());
				layer.setTime(lay.getTime());
				layer.setCount(lay.getCount());
				layers.add(layer);

				if (lay.getCallsList() != null) {
					layer.setLayerDetails(new ArrayList<LayerFred.LayerDetail>());
					for (RawDataProtocol.LayerDetail rawDataLayerDetail : lay.getCallsList()) {
						LayerFred.LayerDetail layerDetail = layer.new LayerDetail();
						layerDetail.setTimestamp(rawDataLayerDetail.getTimestamp());
						layerDetail.setCode(rawDataLayerDetail.getCode());
						layerDetail.setCount(rawDataLayerDetail.getCount());
						layerDetail.setResponseTime(rawDataLayerDetail.getTime());
						layer.getLayerDetails().add(layerDetail);
					}
				}
			}
			transaction.setLayers(layers);
			transactions.add(transaction);

		}
		return transactions;
	}

	public static List<MBeanFred> buildMBeans(List<RawDataProtocol.MBean> rawdataMbeans) {
		List<MBeanFred> mbeanList = new ArrayList<>(rawdataMbeans.size());
		for (RawDataProtocol.MBean rawdataMbean : rawdataMbeans) {
			MBeanFred mbean = new MBeanFred();
			mbean.setAttributeInfoCount(rawdataMbean.getAttributeInfoCount());
			mbean.setCollectingTime(rawdataMbean.getCollectingTime());
			mbean.setObjectName(rawdataMbean.getObjectName());
			mbeanList.add(mbean);

			ArrayList<MBeanFred.AttributeInfo> attributeInfos = new ArrayList<>(rawdataMbean.getAttributeInfoCount());
			mbean.setAttributeInfos(attributeInfos);
			for (RawDataProtocol.MBeanAttributeInfo rawdataAttrInfo : rawdataMbean.getAttributeInfoList()) {
				MBeanFred.AttributeInfo attrInfo = mbean.new AttributeInfo();
				attrInfo.setNameId(rawdataAttrInfo.getNameId());
				attrInfo.setValue(rawdataAttrInfo.getValue());
				attributeInfos.add(attrInfo);
			}
		}
		return mbeanList;
	}
}

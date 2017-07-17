package org.nudge.elasticstack.context.nudge.dto;

import com.nudge.apm.buffer.probe.RawDataProtocol;
import com.nudge.apm.buffer.probe.RawDataProtocol.Layer;
import org.nudge.elasticstack.context.nudge.api.FilterManager;
import org.nudge.elasticstack.context.nudge.api.bean.Filter;
import org.nudge.elasticstack.exception.ExcludedTransactionException;

import java.util.ArrayList;
import java.util.List;

/**
 * Mapping builder for Nudge APM rawdata objects.
 *
 * @author Sarah Bourgeois
 * @author Frederic Massart
 */
public class DTOBuilder {

	public static List<TransactionDTO> buildTransactions(List<RawDataProtocol.Transaction> rawdataTransactions, List<Filter> filters) {
		List<TransactionDTO> transactions = new ArrayList<>(rawdataTransactions.size());

		for (RawDataProtocol.Transaction rawdataTransaction : rawdataTransactions) {

			String name;
			try {
				name = retrieveTransactionCodeName(rawdataTransaction, filters);
			} catch (ExcludedTransactionException e) {
				continue;
			}

			TransactionDTO transaction = new TransactionDTO();
			transaction.setCode(name);
			transaction.setStartTime(rawdataTransaction.getStartTime());
			transaction.setEndTime(rawdataTransaction.getEndTime());
			transaction.setUserIp(rawdataTransaction.getUserIp());
			
			List<LayerDTO> layers = new ArrayList<>();
			for (Layer lay : rawdataTransaction.getLayersList()) {
				LayerDTO layer = new LayerDTO();
				layer.setLayerName(lay.getLayerName());
				layer.setTime(lay.getTime());
				layer.setCount(lay.getCount());
				layers.add(layer);

				if (lay.getCallsList() != null) {
					layer.setCalls(new ArrayList<LayerCallDTO>());
					for (RawDataProtocol.LayerDetail rawDataLayerDetail : lay.getCallsList()) {
						LayerCallDTO layerCall = new LayerCallDTO();
						layerCall.setTimestamp(rawDataLayerDetail.getTimestamp());
						layerCall.setCode(rawDataLayerDetail.getCode());
						layerCall.setCount(rawDataLayerDetail.getCount());
						layerCall.setResponseTime(rawDataLayerDetail.getTime());
						layer.getCalls().add(layerCall);
					}
				}
			}
			transaction.setLayers(layers);
			transactions.add(transaction);

		}
		return transactions;
	}

	public static List<MBeanDTO> buildMBeans(List<RawDataProtocol.MBean> rawdataMBeans) {
		List<MBeanDTO> mbeanList = new ArrayList<>(rawdataMBeans.size());
		for (RawDataProtocol.MBean rawdataMbean : rawdataMBeans) {
			MBeanDTO mbean = new MBeanDTO();
			mbean.setAttributeInfoCount(rawdataMbean.getAttributeInfoCount());
			mbean.setCollectingTime(rawdataMbean.getCollectingTime());
			mbean.setObjectName(rawdataMbean.getObjectName());
			mbeanList.add(mbean);

			ArrayList<MBeanDTO.AttributeInfo> attributeInfos = new ArrayList<>(rawdataMbean.getAttributeInfoCount());
			mbean.setAttributeInfos(attributeInfos);
			for (RawDataProtocol.MBeanAttributeInfo rawdataAttrInfo : rawdataMbean.getAttributeInfoList()) {
				MBeanDTO.AttributeInfo attrInfo = mbean.new AttributeInfo();
				attrInfo.setNameId(rawdataAttrInfo.getNameId());
				attrInfo.setValue(rawdataAttrInfo.getValue());
				attributeInfos.add(attrInfo);
			}
		}
		return mbeanList;
	}

	/**
	 * Retrieve the transaction code name for a Nudge filter.
	 *
	 * @param transaction the rawdata transaction
	 * @param filters retrieved from nudge api
	 * @return
	 *
	 * @throws ExcludedTransactionException
	 */
	private static String retrieveTransactionCodeName(RawDataProtocol.Transaction transaction, List<Filter> filters)
			throws ExcludedTransactionException {
		String code = transaction.getCode();
		Filter filter = FilterManager.findFilter(filters, transaction, code);

		if (filter != null) {
			if (filter.isExclusion()) {
				throw new ExcludedTransactionException();
			}
			code = FilterManager.constructTargetUrl(transaction, transaction.getCode(), filter);
		}
		return code;

	}

}

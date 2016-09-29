package org.nudge.elasticstack.bean.rawdata;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import com.nudge.apm.buffer.probe.RawDataProtocol.Layer;
import com.nudge.apm.buffer.probe.RawDataProtocol.LayerDetail;
import com.nudge.apm.buffer.probe.RawDataProtocol.Transaction;

public class TransactionBuilder {

	static TransactionFRED t;
	private static SimpleDateFormat sdfr = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

	
	public static List<TransactionFRED> buildTransactionLayerList(List<Transaction> rawdataTransList) {
		List<TransactionFRED> transactionList = new ArrayList<TransactionFRED>();
		for (Transaction trans : rawdataTransList) {
			String layerName = t.setLayerName(trans.getCode());
			String date = t.setLayerDate(sdfr.format(trans.getStartTime()));
			long responsetime = t.setLayerResponseTime(trans.getEndTime() - trans.getStartTime());
			TransactionFRED layer = new TransactionFRED(layerName, responsetime, date);
			transactionList.add(layer);
		}
		return transactionList;
	}


	public static List<TransactionFRED> buildTransactionSqlList(List<Transaction> rawdataList){
		List<TransactionFRED> transactionSqlList = new ArrayList<>();
		List<Layer> layer = new ArrayList<>();
		List<LayerDetail> layerDetail = new ArrayList<>();
		
		for (Transaction trans : rawdataList) {
			trans.getLayersList();
			layer.addAll(trans.getLayersList());
		}
		for (Layer lay : layer) {
			lay.getCallsList();
			layerDetail.addAll(lay.getCallsList());
		}
		for (LayerDetail layd : layerDetail) {
			String sqlCode = t.setSqlCode(layd.getCode());
			long sqlCount = t.setSqlCount(layd.getCount());
			long sqlTime =  t.setSqlTime(layd.getTime());
			String sqlTimestamp = t.setSqlTimeStamp(sdfr.format((layd.getTimestamp())));
			TransactionFRED sql = new TransactionFRED(sqlCode, sqlCount, sqlTime, sqlTimestamp);
			transactionSqlList.add(sql);	
		}
		return transactionSqlList;
	}
	
	// geoip method 
	
	
	

}

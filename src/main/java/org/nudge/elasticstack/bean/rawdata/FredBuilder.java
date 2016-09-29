package org.nudge.elasticstack.bean.rawdata;

import com.nudge.apm.buffer.probe.RawDataProtocol;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fred on 29/09/2016.
 */
public class FredBuilder {

    public static List<TransactionFred> buildTransactions(List<RawDataProtocol.Transaction> rawdataTransactions) {
        List<TransactionFred> transactions = new ArrayList<>(rawdataTransactions.size());

        for (RawDataProtocol.Transaction rawdataTransaction : rawdataTransactions) {
            TransactionFred transaction = new TransactionFred();
            transaction.setCode(rawdataTransaction.getCode());

            transactions.add(transaction);
        }
        return transactions;
    }

    public static List<MBeanFred> buildMbeans(List<RawDataProtocol.MBean> mbean) {
        return null;
    }
}

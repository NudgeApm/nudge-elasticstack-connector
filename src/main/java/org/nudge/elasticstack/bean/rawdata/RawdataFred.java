package org.nudge.elasticstack.bean.rawdata;

import java.util.List;

/**
 * Created by Fred on 29/09/2016.
 */
public class RawdataFred {

    List<TransactionFred> transactions;
    List<MBeanFred> mBeans;

    public List<TransactionFred> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionFred> transactions) {
        this.transactions = transactions;
    }

    public List<MBeanFred> getmBeans() {
        return mBeans;
    }

    public void setmBeans(List<MBeanFred> mBeans) {
        this.mBeans = mBeans;
    }
}

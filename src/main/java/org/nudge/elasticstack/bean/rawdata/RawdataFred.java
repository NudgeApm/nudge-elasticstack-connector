package org.nudge.elasticstack.bean.rawdata;

import java.util.List;

/**
 * // TODO use it o skip it
 */
public class RawdataFred {

    private List<TransactionFred> transactions;
    private List<MBeanFred> mBeans;

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

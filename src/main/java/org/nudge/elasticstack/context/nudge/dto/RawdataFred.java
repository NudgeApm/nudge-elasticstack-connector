package org.nudge.elasticstack.context.nudge.dto;

import java.util.List;

/**
 * // TODO use it o skip it
 */
public class RawdataFred {

    private List<TransactionDTO> transactions;
    private List<MBeanDTO> mBeans;

    public List<TransactionDTO> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionDTO> transactions) {
        this.transactions = transactions;
    }

    public List<MBeanDTO> getmBeans() {
        return mBeans;
    }

    public void setmBeans(List<MBeanDTO> mBeans) {
        this.mBeans = mBeans;
    }
}

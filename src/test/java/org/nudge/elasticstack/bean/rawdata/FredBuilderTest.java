package org.nudge.elasticstack.bean.rawdata;

import com.nudge.apm.buffer.probe.RawDataProtocol;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 *
 */
public class FredBuilderTest {

    private static final Logger LOG = Logger.getLogger(FredBuilderTest.class);

    private RawDataProtocol.RawData rawData;

    @Before
    public void readRawdata() {
        try {
            rawData = RawDataProtocol.RawData.parseFrom(this.getClass().getClassLoader().getResourceAsStream("rawdata/collecte_2016-09-29_10-54-01-620_140.dat"));
        } catch (IOException e) {
            LOG.error("Impossible to read the sample rawdata", e);
        }
    }

    @Test
    public void buildTransactions() throws Exception {
        List<TransactionFred> transactionFredList = FredBuilder.buildTransactions(rawData.getTransactionsList());

        //test -assertions
        RawDataProtocol.Transaction expectedTrans = rawData.getTransactionsList().get(0);
        TransactionFred transaction = transactionFredList.get(0);

        Assert.assertEquals(expectedTrans.getCode(), transaction.getCode());
        Assert.assertEquals(expectedTrans.getStartTime(), transaction.getStartTime());
        Assert.assertEquals(expectedTrans.getEndTime(), transaction.getEndTime());


    }

    @Test
    public void buildMbeans() throws Exception {

    }

}
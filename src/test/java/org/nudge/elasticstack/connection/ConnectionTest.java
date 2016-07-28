package org.nudge.elasticstack.connection;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

/**
 * Test for Connection class.
 *
 * @author Frederic Massart
 */
public class ConnectionTest {

    @Test
    public void parseRawdataListResponseTest() {
        // given
        Connection connection = new Connection("dummyURL");
        InputStream responseStream = new ByteArrayInputStream("[\"collecte_2016-07-28_18-45-02-146_94.dat\",\"collecte_2016-07-28_18-45-02-206_86.dat\"]".getBytes());

        // TODO expectedList
        List<String> expectedList = null;

        // when
        List<String> rawdataList = connection.parseRawdataListResponse(responseStream);

        // then
        Assert.assertEquals(expectedList, rawdataList);

    }


}
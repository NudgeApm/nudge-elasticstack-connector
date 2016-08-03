

package org.nudge.elasticstack;

import mapping.Mapping;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import mapping.Mapping;

import javax.ws.rs.core.Response;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author : Frederic Massart
 */
public class DaemonTest {

    private static final Logger LOG = Logger.getLogger(DaemonTest.class);
    private static final String INDEX_TEST = "nudge.test";
    private static final String URL_ELASTIC_TEST = "http://kibana.nudgeapm.io:9200/";
    private static final int TYPE = 1;
   
    @Test
    // TODO migrate this test in MappingTest
    public void pushMapping_test() throws IOException, URISyntaxException {
        // given
        initTransactionBean();

        // when
        Mapping mapping = new Mapping();

        mapping.pushMapping(URL_ELASTIC_TEST, INDEX_TEST, TYPE);
        mapping.pushMapping(URL_ELASTIC_TEST, INDEX_TEST, 1);

        // then
        URL elasticTest = new URL(URL_ELASTIC_TEST + "/" + INDEX_TEST + "/transaction/_mapping");
        HttpURLConnection connection = (HttpURLConnection) elasticTest.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("GET");
        boolean isMappingEffective = analyseResponse(connection.getInputStream(), "not_analyzed");
        Assert.assertTrue(isMappingEffective);
    }

    private void initTransactionBean() throws IOException, URISyntaxException {
        URL elasticTest = new URL(URL_ELASTIC_TEST + "/nudge/transaction");
        HttpURLConnection connection = (HttpURLConnection) elasticTest.openConnection();
        connection.setRequestMethod("GET");
        if (connection.getResponseCode() == Response.Status.BAD_REQUEST.getStatusCode()
                && analyseResponse(connection.getErrorStream(), "{\"type\":\"illegal_argument_exception\",\"reason\":\"No feature for name [transaction]\"}")) {
            putTransaction();
        }
    }

    private boolean analyseResponse(InputStream inputStream, String textToSearch) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        String response = new String(result.toByteArray());
        LOG.info("HTTP Response is : " + response);
        return (response.contains(textToSearch));
    }

    // --------------------------------
    // Helpers methods
    // --------------------------------

    private void putTransaction() throws IOException, URISyntaxException {
        URL elasticTest = new URL(URL_ELASTIC_TEST + "/" + INDEX_TEST + "/transaction/1");
        HttpURLConnection connection = (HttpURLConnection) elasticTest.openConnection();
        connection.setRequestMethod("PUT");
        connection.setDoOutput(true);

        String transactionSample = readJSonFile("org.nudge.elasticstack.json/transaction_sample.json");
        PrintWriter writer = new PrintWriter(connection.getOutputStream());
        writer.append(transactionSample);
        writer.flush();
        writer.close();
        LOG.info("Insert transaction " + connection.getResponseCode() + " - " + connection.getResponseMessage());
    }

    private String readJSonFile(String resourcePath) throws URISyntaxException, IOException {
        URL jsonURL = this.getClass().getClassLoader().getResource(resourcePath);
        return new String(Files.readAllBytes(Paths.get(jsonURL.toURI())), StandardCharsets.UTF_8.toString());
    }

    // --------------------------------
    // Init and teardown stuff
    // --------------------------------

    @Before
    public void init() throws IOException, URISyntaxException {
        LOG.info("--- Init Test -------");
        if (isIndexExists()) {
            dropIndex();
        }
        createIndex();
        LOG.info("--- End Init Test ---");
    }

    @After
    public void after() throws IOException {
        LOG.info("--- Teardown Test -------");
        dropIndex();
        LOG.info("--- End Teardown Test ---");
    }

    private void createIndex() throws IOException, URISyntaxException {
        URL elasticTest = new URL(URL_ELASTIC_TEST + "/" + INDEX_TEST);
        HttpURLConnection connection = (HttpURLConnection) elasticTest.openConnection();
        connection.setRequestMethod("PUT");
        connection.setDoOutput(true);
        PrintWriter writer = new PrintWriter(connection.getOutputStream());

        String createIndex = readJSonFile("org.nudge.elasticstack.json/createindex.json");

        writer.append(createIndex);
        writer.close();
        LOG.info("Init index " + connection.getResponseCode() + " - " + connection.getResponseMessage());
    }

    private void dropIndex() throws IOException {
        URL elasticTest = new URL(URL_ELASTIC_TEST + "/" + INDEX_TEST);
        HttpURLConnection connection = (HttpURLConnection) elasticTest.openConnection();
        connection.setRequestMethod("DELETE");
        LOG.info("Drop index " + connection.getResponseCode() + " - " + connection.getResponseMessage());
        if (connection.getResponseCode() != Response.Status.OK.getStatusCode()) {
            throw new IllegalStateException("Invalid state when delete this index : " + INDEX_TEST);
        }
    }

    private boolean isIndexExists() throws IOException, URISyntaxException {
        URL elasticTest = new URL(URL_ELASTIC_TEST + "/" + INDEX_TEST);
        HttpURLConnection connection = (HttpURLConnection) elasticTest.openConnection();
        connection.setRequestMethod("GET");
        LOG.info("Is index exists " + connection.getResponseCode() + " - " + connection.getResponseMessage());
        int responseCode = connection.getResponseCode();
        if (responseCode == Response.Status.OK.getStatusCode()) {
            return true;
        } else if (responseCode == Response.Status.NOT_FOUND.getStatusCode()) {
            return false;
        } else {
            throw new IllegalStateException("Invalid state when research this index : " + INDEX_TEST);
        }
    }

}


package org.nudge.elasticstack.config;

import org.junit.Assert;
import org.junit.Test;

import java.util.Properties;

/**
 * @author Frederic Massart
 */
public class ConfigurationTest {

    @Test
    public void testEndSlashURL() {
        // given
        Properties properties = defineDefaultProperties();
        String propNudge = "https://monitor.nudge-apm.com";
        String propElastic = "https://elastic.com/";
        String propRawdata = "-10m";
        properties.setProperty("nudge.url", propNudge);
        properties.setProperty("output.elastic.hosts", propElastic);
        properties.setProperty("rawdata.history", propRawdata);
        Configuration configuration = new Configuration(properties);

        // when
        String confNudge = configuration.getNudgeUrl();
        String confElastic = configuration.getOutputElasticHosts();
        String confRawdata = configuration.getRawdataHistory();

        // then
        Assert.assertTrue(confNudge.endsWith("/"));
        Assert.assertTrue(confElastic.endsWith("/"));
    }


    private Properties defineDefaultProperties() {
        Properties properties = new Properties();
        properties.setProperty("nudge.login", "mail");
        properties.setProperty("nudge.password", "password");
        properties.setProperty("nudge.app.ids", "605050b8-6d88-49e5-bdea-165499ee2c4f");
        properties.setProperty("elastic.index", "nudge");
        properties.setProperty("output.elastic.hosts", "http://localhost:9200/");
        properties.setProperty("nudge.url", "https://monitor.nudge-apm.com");
        properties.setProperty("rawdata.history", "-10m");
        return properties;
    }


}
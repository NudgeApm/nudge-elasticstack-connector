package org.nudge.elasticstack.config;

import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class ConfigurationTest {

	@Test
	public void getProperty_environment_variable() {
		//given
		String key = "export_type";
		String expectedValue = "test";
		System.setProperty("nes." + key, expectedValue);
		Configuration conf = new Configuration();

		//when
		String result = conf.getProperty(key, null);

		//then
		assertEquals(expectedValue, result);
	}


	@Test
	public void getProperty_props_conf(){
		//given
		Configuration conf = new Configuration();
		Properties props = conf.getProperties();
		String key = "address";
		String expectedValue = "paris";
		props.setProperty(key, expectedValue);

		// when
		String result = conf.getProperty(key, null);

		// then
		assertEquals(expectedValue, result);
	}


	@Test(expected = IllegalStateException.class)
	public void checkNoNull() {
		//given
		Configuration conf = new Configuration();
		String key = "a.key";
		//when
		conf.checkNotNull(key);
	}

	@Test
	public void loadProperties() throws URISyntaxException {
		// given
		Configuration conf = new Configuration();
		URL resource = this.getClass().getClassLoader().getResource(Configuration.CONF_FILE);
		File confFile = new File(resource.toURI());
		System.out.println("schema : " + confFile);

		String exportType = "file";
		String exportFileDir = "./export/nudge-logstash.log";
		String nudgeUrl = "http://nudgeapm.io";
		String nudgeLogin = "login-user";
		String nudgePwd = "password-user";
		// TODO implement specific test
		String[] metricsValues = { "" };

		// when
		conf.loadProperties(resource.getPath());

		// then
		// test file exists
		// tester parametres mis dans object Properties
		assertEquals(exportType, conf.getExportType());
		assertEquals(exportFileDir, conf.getExportFileDir());
		assertEquals(nudgeUrl, conf.getNudgeUrl());
		assertEquals(nudgeLogin, conf.getNudgeLogin());
		assertEquals(nudgePwd, conf.getNudgePwd());
		assertArrayEquals(metricsValues, conf.getMetrics());
	}




}
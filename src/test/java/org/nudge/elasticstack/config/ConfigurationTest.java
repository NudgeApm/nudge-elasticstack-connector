package org.nudge.elasticstack.config;

import org.hamcrest.core.StringContains;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import java.util.Properties;
import static org.junit.Assert.assertEquals;

public class ConfigurationTest {

	@Rule public final ExpectedException exception = ExpectedException.none();

//	@Test
//	public void propertiesLoadshouldFailWhenNudgeLoginNotProvided() {
//		exception.expect(IllegalArgumentException.class);
//		exception.expectMessage(StringContains.containsString("nudge.login"));
//		new Configuration();
//	}

	@Test
	public void propertiesLoadShouldFailWhenNudgeLoginProvidedButNotPwd() {
		System.setProperty("nes.nudge.login", "any-login");
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(StringContains.containsString("nudge.password"));
		new Configuration();
	}

	@Test
	public void getProperty_environment_variable() {
		// given
		String key = "export_type";
		String expectedValue = "test";
		System.setProperty("nes." + key, expectedValue);
		Configuration conf = new Configuration(true);

		// when
		String result = conf.getProperty(key, null);

		// then
		assertEquals(expectedValue, result);
	}

	@Test
	public void getProperty_props_conf() {
		// given
		Properties props = new Properties();
		String key = "address";
		String expectedValue = "paris";
		props.setProperty(key, expectedValue);
		Configuration conf = new Configuration(props);

		// when
		String result = conf.getProperty(key, null);

		// then
		assertEquals(expectedValue, result);
	}

	@Test(expected = IllegalArgumentException.class)
	public void checkNoNull() {
		// given
		Configuration conf = new Configuration(true);
		String key = "a.key";
		// when
		conf.checkNotNull(key);
	}
//
//	@Test
//	public void loadProperties() throws URISyntaxException {
//		// given
//		URL resource = this.getClass().getClassLoader().getResource(Configuration.CONF_FILE);
//		File confFile = new File(resource.toURI());
//		System.out.println("schema : " + confFile);
//
//		ExportType exportType = ExportType.FILE;
//		String exportFileDir = "./export/nudge-logstash.log";
//		String nudgeUrl = "http://nudgeapm.io";
//		String nudgeLogin = "login-user";
//		String nudgePwd = "password-user";
//		// TODO implement specific test
//		String[] metricsValues = { "" };
//
//		// when
//		Configuration conf = new Configuration(resource.getPath());

		// then
		// test file exists
		// tester parametres mis dans object Properties
//		assertEquals(exportType, conf.getExportType());
//		assertEquals(exportFileDir, conf.getExportFileDir());
//		assertEquals(nudgeUrl + "/", conf.getNudgeUrl());
//		assertEquals(nudgeLogin, conf.getNudgeLogin());
//		assertEquals(nudgePwd, conf.getNudgePwd());
//		assertArrayEquals(metricsValues, conf.getMetrics());
	}


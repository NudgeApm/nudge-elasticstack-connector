package org.nudge.elasticstack.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {

	private Properties props;

	private String exportFileDir;
	private String nudgeUrl;
	private String nudgeLogin;
	private String nudgePwd;
	// TODO remplacer par une structure qui contient : le requêteur de la métrique et le mapper de pojo de l'api
	private String[] metrics;

	public Configuration() {
		loadProperties();
	}

	private void loadProperties() {
		props = new Properties();
		File propsFile = new File("nudge-elasticstack.properties");
		if (propsFile.exists()) {
			try (InputStream propsIS = new FileInputStream("nudge-elasticstack.properties")) {
				props.load(propsIS);
			} catch (IOException e) {
				throw new IllegalStateException(e.getMessage(), e);
			}
		}

		exportFileDir = getProperty("export.file.dir", ".");

		nudgeUrl = getProperty("nudge.url", "https://monitor.nudge-apm.com");
		nudgeLogin = checkNotNull("nudge.login");
		nudgePwd = checkNotNull("nudge.password");

		String metricsValuesList = checkNotNull("metrics.values");
		if (metricsValuesList.contains(","))
			metrics = metricsValuesList.split(",");
		else
			metrics = metricsValuesList.split(";");

	}
	private String checkNotNull(String key) {
		String value = getProperty(key);
		if (value == null) {
			throw new IllegalStateException("You must set the \"" + key + "\" parameter in your properties file.");
		}
		return value;
	}

	public String getExportFileDir() {
		return exportFileDir;
	}

	public String getNudgeUrl() {
		return nudgeUrl;
	}

	public String getNudgeLogin() {
		return nudgeLogin;
	}

	public String getNudgePwd() {
		return nudgePwd;
	}

	public String[] getMetrics() {
		return metrics;
	}

	private String getProperty(String key) {
		return getProperty(key, null);
	}

	private String getProperty(String key, String def) {
		String value = System.getProperty("nes." + key);
		if (value != null) {
			return value;
		}
		value = props.getProperty(key);
		if (value != null) {
			return value;
		}
		return def;
	}
}

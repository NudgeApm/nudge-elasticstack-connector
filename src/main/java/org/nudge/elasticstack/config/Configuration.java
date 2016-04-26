package org.nudge.elasticstack.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {

	public static final String EXPORT_FILE_DIR = "export.file.dir";
	public static final String METRICS_VALUES = "metrics.values";
	public static final String NUDGE_URL = "nudge.url";
	public static final String NUDGE_LOGIN = "nudge.login";
	public static final String NUDGE_PWD = "nudge.password";

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
			try (InputStream propsIS = new FileInputStream(propsFile)) {
				props.load(propsIS);
			} catch (IOException e) {
				throw new IllegalStateException(e.getMessage(), e);
			}
		}

		exportFileDir = getProperty(EXPORT_FILE_DIR, ".");

		nudgeUrl = getProperty(NUDGE_URL, "https://monitor.nudge-apm.com");
		nudgeLogin = checkNotNull(NUDGE_LOGIN);
		nudgePwd = checkNotNull(NUDGE_PWD);

		String metricsValuesList = checkNotNull(METRICS_VALUES);
		if (metricsValuesList.contains(",")) {
			metrics = metricsValuesList.split(",");
		} else {
			metrics = metricsValuesList.split(";");
		}
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

	/**
	 * 
	 * @param key
	 * @param def
	 *          default value
	 * @return the value mathcing the key
	 */
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

	public static void displayOptions() {
		System.out.println(EXPORT_FILE_DIR + " -> Directory where logstash logs should be written");
		System.out.println(NUDGE_URL + " -> URL that should be used to connect to NudgeAPM");
		System.out.println(NUDGE_LOGIN + " -> Login that should be used to connect to NudgeAPM");
		System.out.println(NUDGE_PWD + " -> Password that should be used to connect to NudgeAPM");
		System.out.println(METRICS_VALUES + " -> Metrics that should be collected from NudgeAPM");
	}
}

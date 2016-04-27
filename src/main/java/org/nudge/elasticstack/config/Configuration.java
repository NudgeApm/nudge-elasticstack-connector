package org.nudge.elasticstack.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class Configuration {

	public static final String CONF_FILE = "nudge-logstash.properties";
	public static final String EXPORT_FILE_DIR = "export.file.dir";
	public static final String EXPORT_TYPE = "export.type";
	public static final String METRICS_VALUES = "metrics.values";
	public static final String NUDGE_URL = "nudge.url";
	public static final String NUDGE_LOGIN = "nudge.login";
	public static final String NUDGE_PWD = "nudge.password";


	private Properties properties = new Properties();

	private String exportFileDir;
	private String exportType;
	private String nudgeUrl;
	private String nudgeLogin;
	private String nudgePwd;
	// TODO remplacer par une structure qui contient : le requêteur de la
	// métrique et le mapper de pojo de l'api
	private String[] metrics;

	public Configuration() {
	}

	/**
	 * Load properties with the default conf file, must be placed next to the jar program.
	 */
	public void loadProperties() {
		try {
			Path folderJarPath = Paths.get(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();
			String confFile = folderJarPath.toString() + "/" + CONF_FILE;
			loadProperties(confFile);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

//		jarPath).getParent(), CONF_FILE);
//		loadProperties(CONF_FILE);
	}

	/**
	 * Load properties from a path file.
	 *
	 * @param pathFile
	 * 			path file that define the properties to load
	 */
	public void loadProperties(String pathFile) {
		File propsFile = new File(pathFile);
		boolean propsFileExists = propsFile.exists();
		System.out.println("is props file exist : " + propsFileExists + " for this path : " + propsFile);
		if (propsFileExists) {
			try (InputStream propsIS = new FileInputStream(propsFile)) {
				properties.load(propsIS);
			} catch (IOException e) {
				throw new IllegalStateException(e.getMessage(), e);
			}
		}

		exportType = getProperty(EXPORT_TYPE, "file");
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

	String checkNotNull(String key) {
		String value = getProperty(key, null);
		if (value == null) {
			throw new IllegalStateException("You must set the \"" + key + "\" parameter in your properties file.");
		}
		return value;
	}

	/**
	 * 
	 * @param key
	 * @param defaultValue
	 *            default value
	 * @return the value mathcing the key
	 */
	String getProperty(String key, String defaultValue) {
		String value = System.getProperty("nes." + key);
		if (value != null) {
			return value;
		}
		value = properties.getProperty(key);
		if (value != null) {
			return value;
		}
		return defaultValue;
	}

	public static void displayOptions() {
		System.out.println(EXPORT_FILE_DIR + " -> Directory where logstash logs should be written");
		System.out.println(NUDGE_URL + " -> URL that should be used to connect to NudgeAPM");
		System.out.println(NUDGE_LOGIN + " -> Login that should be used to connect to NudgeAPM");
		System.out.println(NUDGE_PWD + " -> Password that should be used to connect to NudgeAPM");
		System.out.println(METRICS_VALUES + " -> Metrics that should be collected from NudgeAPM");
	}

	Properties getProperties() {
		return properties;
	}

	public String getExportFileDir() {
		return exportFileDir;
	}

	public void setExportFileDir(String exportFileDir) {
		this.exportFileDir = exportFileDir;
	}

	public String getExportType() {
		return exportType;
	}

	public void setExportType(String exportType) {
		this.exportType = exportType;
	}

	public String getNudgeUrl() {
		return nudgeUrl;
	}

	public void setNudgeUrl(String nudgeUrl) {
		this.nudgeUrl = nudgeUrl;
	}

	public String getNudgeLogin() {
		return nudgeLogin;
	}

	public void setNudgeLogin(String nudgeLogin) {
		this.nudgeLogin = nudgeLogin;
	}

	public String getNudgePwd() {
		return nudgePwd;
	}

	public void setNudgePwd(String nudgePwd) {
		this.nudgePwd = nudgePwd;
	}

	public String[] getMetrics() {
		return metrics;
	}

	public void setMetrics(String[] metrics) {
		this.metrics = metrics;
	}
}

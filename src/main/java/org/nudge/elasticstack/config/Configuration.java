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

	public enum ExportType {
		FILE, ELASTIC
	}

	public static final String CONF_FILE = "nudge-logstash.properties";

	public static final String EXPORT_FILE_DIR = "export.file.dir";
	public static final String EXPORT_TYPE = "export.type";
	public static final String NUDGE_URL = "nudge.url";
	public static final String NUDGE_LOGIN = "nudge.login";
	public static final String NUDGE_PWD = "nudge.password";

	public static final String METRICS_APP_IDS = "metrics.app.ids";
	public static final String METRICS_VALUES = "metrics.values";

	private Properties properties = new Properties();
	private String exportFileDir;
	private ExportType exportType;
	private String nudgeUrl;
	private String nudgeLogin;
	private String nudgePwd;
	private String[] metrics;
	private String[] apps;

	public Configuration() {
		loadProperties();
	}

	Configuration(boolean initWithoutLoad) {
		if (!initWithoutLoad) {
			loadProperties();
		}
	}

	Configuration(Properties props) {
		this.properties = props;
	}

	Configuration(String pathFile) {
		loadProperties(pathFile);
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
	}

	/**
	 * Load properties from a path file.
	 *
	 * @param pathFile
	 *          path file that define the properties to load
	 */

	// on veut que le fichier proporties soit a coté du jar.
	// ou variable d'environnement ajouté pour charger le fichier.
	public void loadProperties(String pathFile) {
		File propsFile = new File(pathFile);
		boolean propsFileExists = propsFile.exists();
		if (propsFileExists) {
			try (InputStream propsIS = new FileInputStream(propsFile)) {
				properties.load(propsIS);
			} catch (IOException e) {
				throw new IllegalStateException(e.getMessage(), e);
			}
		}

		String exportTypeStr = getProperty(EXPORT_TYPE, "file");
		if (exportTypeStr == null) {
			exportType = ExportType.FILE;
		} else {
			try {
				exportType = ExportType.valueOf(exportTypeStr.toUpperCase());
			} catch (IllegalArgumentException iae) {
				throw new IllegalArgumentException("Unknown value " + exportTypeStr + " from EXPORT_TYPE parameter");
			}
		}
		exportFileDir = getProperty(EXPORT_FILE_DIR, ".");
		nudgeUrl = getProperty(NUDGE_URL, "https://monitor.nudge-apm.com");
		if (!nudgeUrl.endsWith("/"))
			nudgeUrl += "/";
		nudgeLogin = checkNotNull(NUDGE_LOGIN);
		nudgePwd = checkNotNull(NUDGE_PWD);

		apps = split(checkNotNull(METRICS_APP_IDS));
		//metrics = split(checkNotNull(METRICS_VALUES));
	}

	private String[] split(String composite) {
		return composite.contains(",") ? composite.split(",") : composite.split(";");
	}

	String checkNotNull(String key) {
		String value = getProperty(key, null);
		if (value == null) {
			throw new IllegalArgumentException("You must set the \"" + key + "\" parameter in your properties file.");
		}
		return value;
	}

	/**
	 * 
	 * @param key
	 * @param defaultValue
	 *          default value
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
		System.out.println(METRICS_APP_IDS + " -> Apps id to grab data from NudgeAPM");
	}

	public String getExportFileDir() {
		return exportFileDir;
	}

	public ExportType getExportType() {
		return exportType;
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

	public String[] getAppIds() {
		return apps;
	}
}

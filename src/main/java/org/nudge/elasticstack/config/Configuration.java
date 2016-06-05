package org.nudge.elasticstack.config;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class Configuration {

	private static final Logger LOG = Logger.getLogger(Configuration.class);

	// Configuration files
	public static final String CONF_FILE = "nudge-elastic.properties";
	public static final String NUDGE_URL = "nudge.url";
	public static final String NUDGE_LOGIN = "nudge.login";
	public static final String NUDGE_PWD = "nudge.password";
	public static final String NUDGE_APP_IDS = "nudge.app.ids";
	public static final String ELASTIC_INDEX = "elastic.index";
	public static final String ELASTIC_OUTPUT = "elastic.output";
	public static final String NUDGE_API_ADRESS = "nudge.api.adress";

	// Attributs
	private Properties properties = new Properties();
	private String nudgeUrl;
	private String nudgeLogin;
	private String nudgePwd;
	private String[] apps;
	private String elasticIndex;
	private String elasticOutput;
	private String nudgeApiAdress;

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
	 * Load properties with the default conf file, must be placed next to the
	 * jar program.
	 */

	public void loadProperties() {
		try {
			Path folderJarPath = Paths.get(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI())
					.getParent();
			String confFile = folderJarPath.toString() + "/" + CONF_FILE;
			// TODO FMA arrange this
			if (Files.exists(Paths.get(confFile))) {
				loadProperties(confFile);
			} else {
				URL confURL = ClassLoader.getSystemResource(CONF_FILE);
				if (confURL != null) {
					LOG.debug(CONF_FILE + " found at the classloader root path");
					loadProperties(confURL.getPath());
				}
				LOG.debug(CONF_FILE + " doesn't found at the classloader root path");
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

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
		nudgeUrl = getProperty(NUDGE_URL, "https://monitor.nudge-apm.com");
		if (!nudgeUrl.endsWith("/"))
			nudgeUrl += "/";
		nudgeLogin = checkNotNull(NUDGE_LOGIN);
		nudgePwd = checkNotNull(NUDGE_PWD);
		apps = split(checkNotNull(NUDGE_APP_IDS));
		elasticOutput = checkNotNull(ELASTIC_OUTPUT);
		elasticIndex = checkNotNull(ELASTIC_INDEX);
		nudgeApiAdress = checkNotNull(NUDGE_API_ADRESS);
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
		System.out.println(NUDGE_URL + " -> URL that should be used to connect to NudgeAPM");
		System.out.println(NUDGE_LOGIN + " -> Login that should be used to connect to NudgeAPM");
		System.out.println(NUDGE_PWD + " -> Password that should be used to connect to NudgeAPM");
		System.out.println(NUDGE_APP_IDS + " -> Apps id to grab data from NudgeAPM");
		System.out.println(ELASTIC_INDEX + " -> Name of the elasticSearch index which will be create");
		System.out.println(ELASTIC_OUTPUT + " -> Adress of the elasticSearch which will be use to index");
		System.out.println(NUDGE_API_ADRESS + " -> Adress of Nudge API where rawdata will be request");
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

	public String[] getAppIds() {
		return apps;
	}

	public String getElasticIndex() {
		return elasticIndex;
	}

	public String getElasticOutput() {
		return elasticOutput;
	}

	public String getNudgeApiAdress() {
		return nudgeApiAdress;
	}

}

package org.nudge.elasticstack.config;

/**
 * 
 * @author Sarah Bourgeois
 * @author Frederic Massart
 * 
 */

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
	private static final String CONF_FILE = "nudge-elastic.properties";
	private static final String NUDGE_URL = "nudge.url";
	private static final String NUDGE_LOGIN = "nudge.login";
	private static final String NUDGE_PWD = "nudge.password";
	private static final String NUDGE_APP_IDS = "nudge.app.ids";
	private static final String ELASTIC_INDEX = "elastic.index";
	private static final String OUTUPUT_ELASTIC_HOSTS = "output.elastic.hosts";
	private static final String DRY_RUN = "plugin.dryrun";

	// Attributs
	private Properties properties = new Properties();
	private String nudgeUrl;
	private String nudgeLogin;
	private String nudgePwd;
	private String[] apps;
	private String elasticIndex;
	private String outputElasticHosts;
	private boolean dryRun;

	public Configuration() {
		searchPropertiesFile();
	}

	Configuration(boolean initWithoutLoad) {
		if (!initWithoutLoad) {
			searchPropertiesFile();
		}
	}

	Configuration(Properties props) {
		this.properties = props;
        loadProperties();
	}

	Configuration(String pathFile) {
		loadPropertiesFile(pathFile);
	}

	/**
	 * Load properties with the default conf file, must be placed next to the
	 * jar program.
	 */
	private void searchPropertiesFile() {
		try {
			Path folderJarPath = Paths.get(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI())
					.getParent();
			String confFile = folderJarPath.toString() + "/" + CONF_FILE;
			if (Files.exists(Paths.get(confFile))) {
				loadPropertiesFile(confFile);
			} else {
				URL confURL = ClassLoader.getSystemResource(CONF_FILE);
				if (confURL != null) {
					LOG.debug(CONF_FILE + " found at the classloader root path");
					loadPropertiesFile(confURL.getPath());
				}
				LOG.debug(CONF_FILE + " doesn't found at the classloader root path");
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	private void loadPropertiesFile(String pathFile) {
		File propsFile = new File(pathFile);
		boolean propsFileExists = propsFile.exists();
		if (propsFileExists) {
			try (InputStream propsIS = new FileInputStream(propsFile)) {
				properties.load(propsIS);
			} catch (IOException e) {
				throw new IllegalStateException(e.getMessage(), e);
			}
		}
	    loadProperties();
	}

	private void loadProperties() {
        nudgeUrl = getProperty(NUDGE_URL, "https://monitor.nudge-apm.com");
        if (!nudgeUrl.endsWith("/"))
            nudgeUrl += "/";
        nudgeLogin = checkNotNull(NUDGE_LOGIN);
        nudgePwd = checkNotNull(NUDGE_PWD);
        apps = split(checkNotNull(NUDGE_APP_IDS));
        outputElasticHosts = checkNotNull(OUTUPUT_ELASTIC_HOSTS);
        if (!outputElasticHosts.endsWith("/"))
            outputElasticHosts += "/";
        elasticIndex = checkNotNull(ELASTIC_INDEX);
        dryRun = Boolean.valueOf(getProperty(DRY_RUN, "false"));

    }

	private String[] split(String composite) {
		return composite.contains(",") ? composite.split(",") : composite.split(";");
	}

	String checkNotNull(String key) {
		String value = getProperty(key, null);
		if (value == null) {
			throw new IllegalArgumentException("$You must set the \"" + key + "\" parameter in your properties file.");
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
		System.out.println(OUTUPUT_ELASTIC_HOSTS + " -> Adress of the elasticSearch which will be use to index");
		System.out.println(DRY_RUN + " -> Collect and log, dont push to elasticsearch");
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

	public String getOutputElasticHosts() {
		return outputElasticHosts;
	}

	public boolean getDryRun() {
		return dryRun;
	}
}

package org.nudge.elasticstack;

import json.bean.TimeSerie;
import json.connection.Connection;

import org.nudge.elasticstack.config.Configuration;
import org.nudge.elasticstack.logger.Logger;
import org.nudge.elasticstack.logger.LogstashFileLogger;

import com.nudge.apm.buffer.probe.RawDataProtocol.RawData;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class Daemon {

	private static ScheduledExecutorService scheduler;

	static public void start(Configuration config) {
		scheduler = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
			public Thread newThread(Runnable runnable) {
				Thread thread = new Thread(runnable);
				thread.setName("nudge-es-daemon");
				thread.setDaemon(false);
				return thread;
			}
		});

		scheduler.scheduleAtFixedRate(new DaemonTask(config), 0L, 1L, TimeUnit.MINUTES);
	}

	static class DaemonTask implements Runnable {

		private Configuration config;
		private Logger logger;

		public DaemonTask(Configuration config) {
			this.config = config;
			switch (config.getExportType()) {
			case FILE:
				logger = new LogstashFileLogger();
				break;
			case ELASTIC:
			default:
				throw new IllegalArgumentException("Export type " + config.getExportType() + " not yet implemented");
			}
		}
		
		private static List<String> analyzedFilenames = new ArrayList<>();

		@Override
		public void run() {
			try {
				Connection c = new Connection(config.getNudgeUrl());
				c.login(config.getNudgeLogin(), config.getNudgePwd());
				
				for (String appId : config.getAppIds()) {
					
					List<String> rawdataList = c.requestRawdataList(appId, "-10m");
					
					/*
					if(analyzedFilenames.size() == 0) {
						analyzedFilenames.addAll(rawdataList);
						break;
					}
					*/
				
					// TODO 2: Comparer la liste obtenue avec les précédents fichiers analysés
					for (String rawdataFilename : rawdataList) {
						if(!analyzedFilenames.contains(rawdataFilename)) {
							RawData rawdata = c.requestRawdata(appId, rawdataFilename);
							// TODO 3: Intéger dans elastic search les données du rawdata
							
						}
					}
					
					analyzedFilenames = rawdataList;
				}
					

				// on interroge volontairement les informations avec 5 minutes de retard
				// pour s'assurer que les informations sont à jour côté Nudge
				/* OLD CODE
				Instant sinceInstant = Instant.now().minus(5, ChronoUnit.MINUTES);
				Instant untilInstant = sinceInstant.plus(1, ChronoUnit.MINUTES);

				for (String appId : config.getAppIds()) {
					TimeSerie serie = c.appTimeSerie(appId, sinceInstant, untilInstant, "1m");
					logger.log(serie);
				}
				*/
			} catch (Throwable t) {
				t.printStackTrace();
				scheduler.shutdown();
			}
		}
	}
	
	public static void main(String[] args) {
		Properties props = new Properties();
		props.setProperty(Configuration.METRICS_APP_IDS,	"c709dba6-bf5d-4a03-b1f3-1ca57e6bde95");
		Configuration config = new Configuration(props);
		DaemonTask task = new DaemonTask(config);
		task.run();
	}
	
}

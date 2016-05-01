package org.nudge.elasticstack;

import json.bean.TimeSerie;
import json.connection.Connection;

import org.nudge.elasticstack.config.Configuration;
import org.nudge.elasticstack.logger.Logger;
import org.nudge.elasticstack.logger.LogstashFileLogger;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
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

		@Override
		public void run() {
			try {
				Connection c = new Connection(config.getNudgeUrl());
				c.login(config.getNudgeLogin(), config.getNudgePwd());

				// on interroge volontairement les informations avec 5 minutes de retard
				// pour s'assurer que les informations sont à jour côté Nudge
				Instant sinceInstant = Instant.now().minus(5, ChronoUnit.MINUTES);
				Instant untilInstant = sinceInstant.plus(1, ChronoUnit.MINUTES);

				for (String appId : config.getAppIds()) {
					TimeSerie serie = c.appTimeSerie(appId, sinceInstant, untilInstant, "1m");
					logger.log(serie);
				}
			} catch (Throwable t) {
				t.printStackTrace();
				scheduler.shutdown();
			}
		}
	}
}

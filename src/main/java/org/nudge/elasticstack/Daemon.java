package org.nudge.elasticstack;

import json.NudgeApiPOC;
import org.nudge.elasticstack.config.Configuration;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class Daemon {

	static public void start(Configuration config) {
		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
			public Thread newThread(Runnable runnable) {
				Thread thread = new Thread(runnable);
				thread.setName("nudge-es-daemon");
				thread.setDaemon(false);
				return thread;
			}

		});

		long period = 1L;
		TimeUnit minutes = TimeUnit.MINUTES;
		Duration duration = retrieveDuration(period, minutes);

		scheduler.scheduleAtFixedRate(new DaemonTask(config, duration), 0L, period, minutes);
	}

	static class DaemonTask implements Runnable {

		private Configuration config;
		private Duration duration;

		public DaemonTask(Configuration config, Duration duration) {
			this.config = config;
			this.duration = duration;
		}

		@Override
		public void run() {
			Instant instant = Instant.now();

			/*
			 * for (String metric : config.getMetrics()) {
			 * 
			 * // // TODO request value data from nudge (time shift of 5 mintues
			 * to be sure data are computed by Nudge APM)
			 * 
			 * // // TODO convert data to logstash logs
			 * 
			 * // // TODO write logs
			 * 
			 * }
			 */

			// Integration of devoxx poc #5
			try {
				NudgeApiPOC.start(config, duration, instant);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}

	// recupere la durée à requeter, -> demander à boite noire d'extraire en fonction des metrics demandées.

	public static Duration retrieveDuration(long period, TimeUnit timeUnit) {
		if (timeUnit != null) {
			long nbOfSnd = TimeUnit.SECONDS.convert(period, timeUnit);
			return Duration.ofSeconds(nbOfSnd);
		}
		return null;
	}


}

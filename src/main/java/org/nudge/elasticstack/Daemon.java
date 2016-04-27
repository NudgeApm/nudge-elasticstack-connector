package org.nudge.elasticstack;

import json.NudgeApiPOC;
import org.nudge.elasticstack.config.Configuration;

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
		scheduler.scheduleAtFixedRate(new DaemonTask(config), 0L, 10L, TimeUnit.SECONDS);
	}

	static class DaemonTask implements Runnable {

		private Configuration config;

		public DaemonTask(Configuration config) {
			this.config = config;
		}

		@Override
		public void run() {
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
				NudgeApiPOC.start(null);
			} catch (Throwable t) {
			t.printStackTrace();
			}

			

		}

	}

}

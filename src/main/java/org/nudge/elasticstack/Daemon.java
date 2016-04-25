package org.nudge.elasticstack;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.nudge.elasticstack.config.Configuration;

public class Daemon implements Runnable {

	private Configuration config;

	public Daemon() {
		config = new Configuration();
	}

	public void run() {
		for (String metric : config.getMetrics()) {

			// // TODO request value data from nudge (time shift of 5 mintues to be sure data are computed by Nudge APM)
			
			// // TODO map data to logs via the mapper
			
			// // TODO write logs
			
		}
	}
	static public void start() {
		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
			public Thread newThread(Runnable runnable) {
				Thread thread = new Thread(runnable);
				thread.setName("nudge-es-daemon");
				thread.setDaemon(false);
				return thread;
			}
		});
		scheduler.scheduleWithFixedDelay(new Daemon(), 0L, 1L, TimeUnit.SECONDS);
	}

}

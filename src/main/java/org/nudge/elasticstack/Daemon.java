package org.nudge.elasticstack;

/**
 * @author Sarah Bourgeois
 * @author Frederic Massart
 *
 * Description : Class which permits to send rawdatas to elasticSearch with -startDeamon
 */

import com.nudge.apm.buffer.probe.RawDataProtocol.Dictionary;
import com.nudge.apm.buffer.probe.RawDataProtocol.MBean;
import com.nudge.apm.buffer.probe.RawDataProtocol.RawData;
import com.nudge.apm.buffer.probe.RawDataProtocol.Transaction;
import mapping.Mapping;
import org.apache.log4j.Logger;
import org.nudge.elasticstack.config.Configuration;
import org.nudge.elasticstack.connection.Connection;
import org.nudge.elasticstack.json.bean.EventMBean;
import org.nudge.elasticstack.json.bean.EventSQL;
import org.nudge.elasticstack.json.bean.EventTransaction;
import org.nudge.elasticstack.type.Mbean;
import org.nudge.elasticstack.type.Sql;
import org.nudge.elasticstack.type.TransactionLayer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")

public class Daemon {
	private static final Logger LOG = Logger.getLogger("Connector : ");
	private static ScheduledExecutorService scheduler;
	private static List<String> analyzedFilenames = new ArrayList<>();
	private static final long ONE_MIN = 60000;

	/**
	 * Description : Launcher Deamon.
	 * 
	 * @param config
	 *
	 */
	public static void start(Configuration config) {
		scheduler = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
			public Thread newThread(Runnable runnable) {
				Thread thread = new Thread(runnable);
				thread.setName("nudge-es-daemon");
				thread.setDaemon(false);
				return thread;
			}
		});
		scheduleDaemon(scheduler, config);
	}

	private static void scheduleDaemon(ScheduledExecutorService scheduler, Configuration config) {
		scheduler.scheduleAtFixedRate(new DaemonTask(config), 0L, 1L, TimeUnit.MINUTES);
	}

	public static void stop() {
		scheduler.shutdown();
	}

	protected static class DaemonTask implements Runnable {
		private Configuration config;

		DaemonTask(Configuration config) {
			this.config = config;
		}

		/**
		 * Description : Call connector methods and run it
		 */
		@Override
		public void run() {
			try {
				// Connection and load configuration
				Connection c = new Connection(config.getNudgeUrl());
				c.login(config.getNudgeLogin(), config.getNudgePwd());
				for (String appId : config.getAppIds()) {
					List<String> rawdataList = c.requestRawdataList(appId, "-10m");
					// analyse files, comparaison and push
					for (String rawdataFilename : rawdataList) {
						if (!analyzedFilenames.contains(rawdataFilename)) {
							RawData rawdata = c.requestRawdata(appId, rawdataFilename);

							// ==============================
							// Type : Transaction and Layer
							// ==============================
							TransactionLayer tl = new TransactionLayer();
							List<Transaction> transactions = rawdata.getTransactionsList();
							List<EventTransaction> events = tl.buildTransactionEvents(transactions);
							for (EventTransaction eventTrans : events) {
								tl.nullLayer(eventTrans);
							}
							List<String> jsonEvents = tl.parseJson(events);
							tl.sendToElastic(jsonEvents);

							// ===========================
							// Type : MBean
							// ===========================
							Mbean mb = new Mbean();
							List<MBean> mbean = rawdata.getMBeanList();
							Dictionary dictionary = rawdata.getMbeanDictionary();
							List<EventMBean> eventsMBeans = mb.buildMbeanEvents(mbean, dictionary);
							List<String> jsonEvents2 = mb.parseJsonMBean(eventsMBeans);
							mb.sendElk(jsonEvents2);

							// ===========================
							// Type : SQL
							// ===========================
							Sql s = new Sql();
							List<EventSQL> sql = s.buildSqlEvents(transactions);
							List<String> jsonEventsSql = s.parseJsonSQL(sql);
							s.sendSqltoElk(jsonEventsSql);

							// ===========================
							// Mapping
							// ===========================
							Mapping mapping = new Mapping();
							// Transaction update mapping
							mapping.pushMapping(config, 1);
							// Sql update mapping
							mapping.pushMapping(config, 2);
							// Mbean update mapping
							mapping.pushMapping(config, 3);
						}
					}
					analyzedFilenames = rawdataList;
				}
			} catch (Throwable t) {
				LOG.fatal("The daemon has encountered a crash error", t);
				if (null != scheduler) {
					// Reschedule the daemonTask
					LOG.info("Restart the daemon in " + ONE_MIN + "ms");
					try {
						Thread.sleep(ONE_MIN);
					} catch (InterruptedException e) {
						LOG.warn("Interrupted before a daemon restart", e);
					}
					LOG.info("Restarting daemon ...");
					scheduleDaemon(scheduler, config);
				}
			}
		}
	}

} // End of class

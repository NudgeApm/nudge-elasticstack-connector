package org.nudge.elasticstack;

import com.nudge.apm.buffer.probe.RawDataProtocol.Dictionary;
import com.nudge.apm.buffer.probe.RawDataProtocol.RawData;
import org.apache.log4j.Logger;
import org.nudge.elasticstack.connection.ElasticConnection;
import org.nudge.elasticstack.connection.NudgeApiConnection;
import org.nudge.elasticstack.context.elasticsearch.bean.EventMBean;
import org.nudge.elasticstack.context.elasticsearch.bean.GeoLocation;
import org.nudge.elasticstack.context.elasticsearch.bean.GeoLocationWriter;
import org.nudge.elasticstack.context.elasticsearch.bean.NudgeEvent;
import org.nudge.elasticstack.context.elasticsearch.builder.GeoLocationElasticPusher;
import org.nudge.elasticstack.context.elasticsearch.builder.MBean;
import org.nudge.elasticstack.context.elasticsearch.builder.TransactionSerializer;
import org.nudge.elasticstack.context.elasticsearch.mapping.Mapping;
import org.nudge.elasticstack.context.nudge.api.bean.Filter;
import org.nudge.elasticstack.context.nudge.dto.DTOBuilder;
import org.nudge.elasticstack.context.nudge.dto.MBeanDTO;
import org.nudge.elasticstack.context.nudge.dto.TransactionDTO;
import org.nudge.elasticstack.exception.NudgeESConnectorException;
import org.nudge.elasticstack.service.GeoLocationService;
import org.nudge.elasticstack.service.NudgeAPIService;
import org.nudge.elasticstack.service.impl.GeoFreeGeoIpImpl;
import org.nudge.elasticstack.service.impl.NudgeAPIServiceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * Daemon task that grab data from Nudge APM API and sends it to ES.
 *
 * @author Sarah Bourgeois
 * @author Frederic Massart
 * @author Thomas Arnaud
 */
class Daemon {

	private static final Logger LOG = Logger.getLogger(Daemon.class);
	private static ScheduledExecutorService scheduler;
	private static List<String> analyzedFilenames = new ArrayList<>();

	/**
	 * Launcher Deamon.
	 *
	 * @param config
	 */
	static void start(Configuration config) {
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

	static void stop() {
		scheduler.shutdown();
	}

	private static void scheduleDaemon(ScheduledExecutorService scheduler, Configuration config) {
		scheduler.scheduleAtFixedRate(new DaemonTask(config), 0L, 1L, TimeUnit.MINUTES);
	}

	protected static class DaemonTask implements Runnable {
		private final Configuration config;
		private final GeoLocationService geoLocationService;
		private final ElasticConnection esCon;
		private final NudgeApiConnection nudgeApiCon;
		private final NudgeAPIService nudgeAPIService;
		// TODO Should be size limited => replace with a cache
		private final Map<String, GeoLocation> geoLocationsMap;

		private final TransactionSerializer transactionLayer;
		private String currentIndex = null;

		DaemonTask(Configuration config) {
			this.config = config;
			geoLocationService = new GeoFreeGeoIpImpl();
			geoLocationsMap = new HashMap<>();
			// NudgeApiConnection and load configuration
			nudgeApiCon = new NudgeApiConnection(config.getNudgeUrl(), config.getNudgeApiToken());
			nudgeAPIService = new NudgeAPIServiceImpl(nudgeApiCon);
			try {
				this.esCon = new ElasticConnection(config.getElasticHostURL());
			} catch (Exception e) {
				throw new IllegalStateException("An error occurs during the first connection to elasticsearch", e);
			}
			transactionLayer = new TransactionSerializer();
		}

		/**
		 * Drives connections to Nudge API and ES and handle data manipulation
		 */
		@Override
		public void run() {
			try {
				String esIndex = config.getElasticIndex() + "-" + Utils.getIndexSuffix();
				if (!esIndex.equals(currentIndex)) {
					esCon.createAndUseIndex(esIndex);
					// Mapping
					Mapping mapping = new Mapping(esCon);
					mapping.pushMappings();
					currentIndex = esIndex;
				}

				for (String appId : config.getAppIds()) {
					List<String> rawdataList = nudgeApiCon.getRawdataList(appId, "-10m");
					// analyse files, comparaison and push
					for (String rawdataFilename : rawdataList) {
						if (!analyzedFilenames.contains(rawdataFilename)) {
							RawData rawdata = nudgeApiCon.getRawdata(appId, rawdataFilename);
							String hostname = rawdata.getHostname();
							String nudgeConfigHostname = nudgeAPIService.retrieveConfiguredHostName(appId, hostname);
							String appName = nudgeAPIService.retrieveAppName(appId);

							// Request Filters
							List<Filter> filters = nudgeApiCon.requestFilters(appId);


							// ==============================
							// Type : Transaction and Layer
							// ==============================
							List<TransactionDTO> esTransactions = DTOBuilder.buildTransactions(rawdata.getTransactionsList(), filters);

							List<NudgeEvent> events = transactionLayer.serialize(appId, appName, hostname, nudgeConfigHostname, esTransactions);
//							for (TransactionEvent eventTrans : events) { // WTF
//								transactionLayer.nullLayer(eventTrans);
//							}
							List<String> jsonEvents = transactionLayer.serialize(events);
							transactionLayer.sendToElastic(jsonEvents);

							// ===========================
							// Type : MBean
							// ===========================
							MBean mb = new MBean();
							List<com.nudge.apm.buffer.probe.RawDataProtocol.MBean> mbean = rawdata.getMBeanList();

							List<MBeanDTO> mBeans = DTOBuilder.buildMBeans(mbean);

							Dictionary dictionary = rawdata.getMbeanDictionary();
							List<EventMBean> eventsMBeans = mb.buildMbeanEvents(appId, mBeans, dictionary);
							List<String> jsonEvents2 = mb.parseJsonMBean(eventsMBeans);
							mb.sendElk(jsonEvents2);

							// ===========================
							// Type : SQL
							// ===========================
//							LayerTransformer s = new LayerTransformer();
//							List<SQLEvent> sql = s.buildSQLEvents(appId, appName, hostname, nudgeConfigHostname, esTransactions);
//							List<String> jsonEventsSql = s.parseJsonSQL(sql);
//							s.sendSqltoElk(jsonEventsSql);

							// ===========================
							// GeoLocalation
							// ===========================
							List<GeoLocation> geoLocations = new ArrayList<>();
							GeoLocationElasticPusher gep = new GeoLocationElasticPusher();
							try {
								for (TransactionDTO transaction : esTransactions) {
									String userIp = transaction.getUserIp();
									if (userIp != null && !"".equals(userIp)) {
										GeoLocation geoLocation = geoLocationsMap.get(userIp);
										if (geoLocation == null) {
											LOG.debug("looking for " + userIp);
											geoLocation = geoLocationService.requestGeoLocationFromIp(userIp);
											geoLocationsMap.put(userIp, geoLocation);
										}
										geoLocations.add(geoLocation);
									}
								}
							} catch (NudgeESConnectorException e) {
								LOG.error("Failed to consider geolocation", e);
							}
							List<GeoLocationWriter> location = gep.buildLocationEvents(geoLocations, esTransactions);
							List<String> json = gep.parseJsonLocation(location);
							gep.sendElk(json);
						}
					}
					analyzedFilenames = rawdataList;
				}
			} catch (Throwable t) {
				LOG.error("Uncaught error", t);
			}
		}
	}

}

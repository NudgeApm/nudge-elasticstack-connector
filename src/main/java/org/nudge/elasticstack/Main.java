package org.nudge.elasticstack;

import org.apache.log4j.Logger;

/**
 * @author Sarah Bourgeois
 * @author Frederic Massart
 * 
 * Entry point for NudgeApm - ElasticStack connector
 */
public class Main {

	private static final Logger LOG = Logger.getLogger(Main.class);

	public static void main(String[] args) {

		System.out.println("      Nudge-Elastic-Connector : starting ... ");
		System.out.println("------------------------------------------- \n");
		if (args.length < 1) {
			usage();
			System.exit(1);
		}

		if ("-startDaemon".equals(args[0])) {
			try {
				Configuration conf = Configuration.getInstance();
				Daemon.start(conf);
			} catch (IllegalStateException ise) {
				LOG.error("Failed to start daemon", ise);
			}
			return;
		}

		if ("-stopDaemon".equals(args[0])) {
			// TODO
			System.out.println("Not yet implemented");
			return;
		}

		if ("-visualizationModel".equals(args[0])) {
			// TODO
			System.out.println("Not yet implemented");
			return;
		}

		if ("-properties".equals(args[0])) {
			Configuration.displayOptions();
			return;
		}
		usage();
		System.exit(1);
	}

	private static void usage() {
		System.out.println("Usage: java -jar nudge-elasticstack.jar [-command] [args]");
		System.out.println("The following commands are available:");
		System.out.println("    -startDaemon                  Starts daemon");
		System.out.println("    -stopDaemon                   Stops daemon");
		System.out.println("    -visualizationModel [metric]  Prints kibana visualization model for a specific metric");
		System.out.println("    -properties                   Prints list of this program properties");
	}

}

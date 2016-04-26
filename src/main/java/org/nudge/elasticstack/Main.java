package org.nudge.elasticstack;

import org.nudge.elasticstack.config.Configuration;

/**
 * Entry point for NudgeApm - ElasticStack connector
 */
public class Main {

	public static void main(String[] args) {
		if (args.length < 1) {
			usage();
			System.exit(1);
		}

		if ("-startDaemon".equals(args[0])) {
			try {
				Daemon.start(new Configuration());
			} catch (IllegalStateException ise) {
				System.err.println("Failed to start daemon: " + ise.getMessage());
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

package org.nudge.elasticstack.connection;

/**
 * HttpMethods used in the connector.
 */
public enum HttpMethod {

	GET, PUT;

	@Override
	public String toString() {
		return super.toString().toUpperCase();
	}
}

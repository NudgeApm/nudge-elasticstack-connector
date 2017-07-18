package org.nudge.elasticstack.context.elasticsearch.bean;

/**
 * List of all type of event that the connector pushes to ES.
 */
public enum EventType {

	// TODO FMA add JAX-WS layer here

	TRANSACTION("transaction"), MBEAN("mbean"), SQL("sql"), GEO_LOC("geolocation");

	private final String type;

	EventType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return type;
	}
}

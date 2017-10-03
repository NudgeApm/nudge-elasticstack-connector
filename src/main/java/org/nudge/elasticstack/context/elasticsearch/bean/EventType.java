package org.nudge.elasticstack.context.elasticsearch.bean;

/**
 * List of all type of event that the connector pushes to ES.
 */
public enum EventType {

	// TODO FMA add JAX-WS layer here

	GEO_LOC("geolocation"),
	JAVA("layer_java"),
	JAX_WS("layer_jax-ws"),
	JMS("layer_jms"),
	MBEAN("mbean"),
	SQL("layer_sql"),
	TRANSACTION("transaction");

	private final String type;

	EventType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return type;
	}
}

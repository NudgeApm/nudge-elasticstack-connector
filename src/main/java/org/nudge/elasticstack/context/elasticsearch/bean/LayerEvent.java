package org.nudge.elasticstack.context.elasticsearch.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Elastic
 * @author Frederic Massart
 */
public class LayerEvent extends NudgeEvent {

	public LayerEvent(EventType type) {
		super.setType(type);
	}

	// ===========================
	// Getters and Setters
	// ===========================

	@Override
	@JsonProperty("layer_code")
	public String getName() {
		return super.getName();
	}

	public String getTransactionId() {
		return super.getTransactionId();
	}

	@JsonProperty("layer_count")
	public long getCount() {
		return super.getCount();
	}

	@JsonProperty("layer_responseTime")
	public long getResponseTime() {
		return super.getResponseTime();
	}

}

package org.nudge.elasticstack.context.elasticsearch.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Sarah Bourgeois
 * @author Frederic Massart
 */
public class SQLEvent extends NudgeEvent {

	public SQLEvent() {
		super.setType(EventType.SQL);
	}

	// ===========================
	// Getters and Setters
	// ===========================

	@Override
	@JsonProperty("sql_code")
	public String getName() {
		return super.getName();
	}

	public String getTransactionId() {
		return super.getTransactionId();
	}

	@JsonProperty("sql_count")
	public long getCount() {
		return super.getCount();
	}

	@JsonProperty("sql_responseTime")
	public long getResponseTime() {
		return super.getResponseTime();
	}

}

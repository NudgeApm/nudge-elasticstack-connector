package org.nudge.elasticstack.json.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Sarah Bourgeois
 * 		   Frederic Massart
 * 
 *         Description : Build SQL insert
 */
public class EventSQL extends NudgeEvent {

	public EventSQL(String timestamp, String name, long count, long responseTime) {
		super(name, responseTime, timestamp, count, "sql");
	}

	// ===========================
	// Getters and Setters
	// ===========================

	@Override
	@JsonProperty("sql_code")
	public String getName() {
		return super.getName();
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

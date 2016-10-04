package org.nudge.elasticstack.json.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Sarah Bourgeois
 * 		   Frederic Massart
 * 
 *         Description : Build SQL insert
 */
public class EventSQL extends NudgeEvent {

	private  String transactionId;
	
	
	public EventSQL(String timestamp, String name, long count, long responseTime, String transactionId) {
		super(name, responseTime, timestamp, count, "sql", transactionId);
		this.setTransactionId(transactionId);
	}

	// ===========================
	// Getters and Setters
	// ===========================
	
	
	@Override
	@JsonProperty("sql_code")
	public String getName() {
		return super.getName();
	}

	public  String getTransactionId() {
		return transactionId;
	}

	public  void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
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

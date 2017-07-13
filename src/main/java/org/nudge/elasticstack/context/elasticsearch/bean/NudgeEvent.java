package org.nudge.elasticstack.context.elasticsearch.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author : Sarah Bourgeois
 * @author : Frederic Massart
 */
public abstract class NudgeEvent {

	private String appId;
	private String host;
	private String hostname;
	private String date;
	private String name;
	private long responseTime;
	private long count;
	private EventType type;
	private String transactionId;

	NudgeEvent() {
	}

	@Override
	public String toString() {
		return "NudgeEvent{" +
				"appId='" + appId + '\'' +
				", host='" + host + '\'' +
				", hostname='" + hostname + '\'' +
				", date='" + date + '\'' +
				", name='" + name + '\'' +
				", responseTime=" + responseTime +
				", count=" + count +
				", type='" + type + '\'' +
				", transactionId='" + transactionId + '\'' +
				'}';
	}

	// =========================
	//  Getters and Setters
	// =========================


	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	@JsonProperty("@timestamp")
	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	@JsonProperty("transaction_name")
	public String getName() {
		return name;
	}

	public String getAppId() {
		return appId;
	}

	public void setName(String name) {
		this.name = name;
	}

	public EventType getType() {
		return type;
	}

	public void setType(EventType type) {
		this.type = type;
	}

	@JsonProperty("transaction_responseTime")
	public long getResponseTime() {
		return responseTime;
	}

	public void setResponseTime(long responseTime) {
		this.responseTime = responseTime;
	}

	@JsonProperty("transaction_count")
	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}
	
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getTransactionId() {
		return transactionId;
	}

}

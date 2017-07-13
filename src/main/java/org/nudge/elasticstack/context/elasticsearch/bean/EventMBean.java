package org.nudge.elasticstack.context.elasticsearch.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.nudge.elasticstack.context.elasticsearch.EventType;

/**
 * @author : Sarah Bourgeois
 * @author : Frederic Massart
 * 
 *         Description : Build Manage-Bean insertion
 */

public class EventMBean {

	private String appId;
	private String nameMbean;
	private double valueMbean;
	private EventType type;
	private String objectName;
	private int countAttribute;
	private String collectingTime;

	public EventMBean(String appId, String nameMbean, String objectName, EventType type, double valueMbean, String collectingTime,
					  int countAttribute) {
		this.appId = appId;
		this.nameMbean = nameMbean;
		this.valueMbean = valueMbean;
		this.collectingTime = collectingTime;
		this.countAttribute = countAttribute;
		this.objectName = objectName;
		this.type = type;
	}
	
	// ===========================
	// Getters and Setters
	// ===========================

	public String getAppId() {
		return appId;
	}

	public EventType getType() {
		return type;
	}

	@JsonProperty("mbean_name")
	public String getObjectName() {
		return objectName;
	}

	@JsonProperty("@timestamp")
	public String getCollectingTime() {
		return collectingTime;
	}

	@JsonProperty("mbean_value")
	public double getValueMbean() {
		return valueMbean;
	}

	@JsonProperty("mbean_count")
	public int getCountAttribute() {
		return countAttribute;
	}

	@JsonProperty("mbean_attributename")
	public String getNameMbean() {
		return nameMbean;
	}

	public void setNameMbean(String name) {
		this.nameMbean = name;
	}
}

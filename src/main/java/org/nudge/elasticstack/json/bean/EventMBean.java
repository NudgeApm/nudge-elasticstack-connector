package org.nudge.elasticstack.json.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author : Sarah Bourgeois
 * @author : Frederic Massart
 * 
 *         Description : Build Manage-Bean insertion
 */

public class EventMBean {

	// MBean attribute info
	private String nameMbean;
	private double valueMbean;
	private String type;
	private String objectName;
	private int countAttribute;
	private String collectingTime;
	private static String transactionId;

	public EventMBean(String nameMbean, String objectName, String type, double valueMbean, String collectingTime,
			int countAttribute, String transactionId) {
		this.setNameMbean(nameMbean);
		this.setValueMbean(valueMbean);
		this.setCollectingTime(collectingTime);
		this.setCountAttribute(countAttribute);
		this.setObjectName(objectName);
		this.setType(type);
		EventMBean.getTransactionId();
	}
	
public	String toString() {
	return "transactionId" + transactionId;
}

	// ===========================
	// Getters and Setters
	// ===========================

	public String getType() {
		return type;
	}

	public static String getTransactionId() {
		return transactionId;
	}

	public static void setTransactionId(String transactionId) {
		EventMBean.transactionId = transactionId;
	}

	public String setType(String type) {
		return this.type = type;
	}

	@JsonProperty("mbean_name")
	public String getObjectName() {
		return objectName;
	}

	public String setObjectName(String objectName) {
		return this.objectName = objectName;
	}

	@JsonProperty("@timestamp")
	public String getCollectingTime() {
		return collectingTime;
	}

	public String setCollectingTime(String collectingTime) {
		return this.collectingTime = collectingTime;
	}

	@JsonProperty("mbean_value")
	public double getValueMbean() {
		return valueMbean;
	}

	public double setValueMbean(double valueMbean) {
		return this.valueMbean = valueMbean;
	}

	@JsonProperty("mbean_count")
	public int getCountAttribute() {
		return countAttribute;
	}

	public int setCountAttribute(int countAttribute) {
		return this.countAttribute = countAttribute;
	}

	@JsonProperty("mbean_attributename")
	public String getNameMbean() {
		return nameMbean;
	}

	public String setNameMbean(String nameMbean) {
		return this.nameMbean = nameMbean;
	}

}

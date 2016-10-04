package org.nudge.elasticstack.json.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


/**
 * @author Sarah Bourgeois
 * @author Frederic Massart
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeoLocation {

	private double latitude;
	private double longitude;
	private long responseTime;
	private String type;
	private  String transactionId;

	// =====================
	// Getters and Setters
	// =====================

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public long getResponseTime() {
		return responseTime;
	}

	public void setResponseTime(long responseTime) {
		this.responseTime = responseTime;
	}

	public String getClientlocation() {
		return latitude + "," + longitude;
	}
	
	public  String getTransactionId() {
		return transactionId;
	}

	public  void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}


} // End of class

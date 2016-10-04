package org.nudge.elasticstack.json.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Sarah Bourgeois
 * @author Frederic Massart
 */

public class GeoLocationWriter {
	@JsonIgnoreProperties(ignoreUnknown = true)
	private double latitude;
	private double longitude;
	private String responseTime;
	private String type;
	protected static String transactionId;

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		GeoLocationWriter.transactionId = transactionId;
	}

	public GeoLocationWriter(double latitude, double longitude, String location, String type, String responseTime, String transactionId)
	{
		this.setLatitude(latitude);
		this.setLongitude(longitude);
		this.setResponseTime(responseTime);
		this.setType(type);
		this.setTransactionId(transactionId);
	}

	// ==================
	// Getters and Setters
	// ====================

	@JsonProperty("@timestamp")
	public String getResponseTime() {
		return responseTime;
	}

	public String setResponseTime(String responseTime) {
		return this.responseTime = responseTime;
	}

	@JsonProperty("geolocation_latitude")
	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	@JsonProperty("geolocation_longitude")
	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	@JsonProperty("geoPoint")
	public String getLocation() {
		return latitude + "," + longitude;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}


}

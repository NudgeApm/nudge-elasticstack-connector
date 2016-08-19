package org.nudge.elasticstack.json.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Sarah Bourgeois
 * @author Frederic Massart
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeoLocation {

	private double latitude;
	private double longitude;
	private String clientlocation;
	private long responseTime;
	private String type;

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

	public String setClientlocation(String clientlocation) {
		return this.clientlocation = clientlocation;
	}

} // End of class

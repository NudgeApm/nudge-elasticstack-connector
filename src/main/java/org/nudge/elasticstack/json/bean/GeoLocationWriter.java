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
	private String location;
	private String responseTime;
	private String type;

	public GeoLocationWriter(double latitude, double longitude, String location, String type, String responseTime)
	{
		this.setLatitude(latitude);
		this.setLongitude(longitude);
		this.setLocation(location);
		this.setResponseTime(responseTime);
		this.setType(type);
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

	public void setLocation(String location) {
		this.location = location;
	}

} // End of class

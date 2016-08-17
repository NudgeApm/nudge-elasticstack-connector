package org.nudge.elasticstack.json.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Sarah Bourgeois
 * @author Frederic Massart
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeoLocation {
	private String ip;
	private double  latitude;
	private double longitude;
	private String location;
	private String type;


	// =====================
	// Getters and Setters 
	// =====================
	// Ip adress
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	// Latitude

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	// longitude

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
//	@JsonProperty("geolocation")
//	public String getLocation() {
//		return latitude + longitude;
//	}

	
	@JsonProperty("type")
	public String getType() {
		return "geolocation";
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setLocation(String location) {
		this.location = location;
	}
	
	


} // End of class

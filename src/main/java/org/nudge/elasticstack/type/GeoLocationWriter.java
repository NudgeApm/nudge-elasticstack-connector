package org.nudge.elasticstack.type;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Sarah Bourgeois
 * @author Frederic Massart
 */

public class GeoLocationWriter{
	@JsonIgnoreProperties(ignoreUnknown = true)
	String ip;
	double latitude;
	double longitude;
	String type;

	
	public GeoLocationWriter(String ip, double latitude, double longitude) {
	this.setIp(ip);
	this.setLatitude(latitude);
	this.setLongitude(longitude);
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public String getIp() {
		return ip;
	}


	public void setIp(String ip) {
		this.ip = ip;
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
	
//	public String toString() {
//		return " ip : " + ip + "  latitude :" + latitude + "  longitude :" + longitude;
//	}

	// ==================
	// Getters and Setters
	// ====================


} // End of class

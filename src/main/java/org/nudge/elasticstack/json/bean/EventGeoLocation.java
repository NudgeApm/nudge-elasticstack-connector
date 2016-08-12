package org.nudge.elasticstack.json.bean;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author Sarah Bourgeois
 * 		   Frederic Massart
 *
 */


public class EventGeoLocation {

// UserIp attributes
private List<String> userIp;
private String type;

public EventGeoLocation(List<String> geoData, String type) {
	this.setUserIp(geoData);
}

// =======================
// Getters and Setters 
// =======================

@JsonProperty("location_geoip")
public List<String> getUserIp() {
	return userIp;
}

public void setUserIp(List<String> geoData) {
	this.userIp = geoData;
}

public String getType() {
	return type;
}

public void setType(String type) {
	this.type = type;
}



	
} // End of class

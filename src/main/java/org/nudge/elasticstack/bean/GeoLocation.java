package org.nudge.elasticstack.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Sarah Bourgeois
 * @author Frederic Massart
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class GeoLocation {

    String ip;
    String latitude;
    String longitude;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}

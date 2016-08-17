package org.nudge.elasticstack.service;

import java.io.IOException;

import org.nudge.elasticstack.json.bean.GeoLocation;


/**
 * 
 * @author Sarah Bourgeois
 * @author Frederic Massart
 *
 */
public interface GeoLocationService {

    public GeoLocation requestGeoLocationFromIp(String ip) throws IOException;
}

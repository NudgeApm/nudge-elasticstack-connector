package org.nudge.elasticstack.service;

import org.nudge.elasticstack.context.elasticsearch.json.bean.GeoLocation;

import java.io.IOException;


/**
 * 
 * @author Sarah Bourgeois
 * @author Frederic Massart
 *
 */
public interface GeoLocationService {

    public GeoLocation requestGeoLocationFromIp(String ip) throws IOException;
}

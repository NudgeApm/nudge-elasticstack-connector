package org.nudge.elasticstack.service;

import org.nudge.elasticstack.exception.NudgeESConnectorException;
import org.nudge.elasticstack.context.elasticsearch.bean.GeoLocation;


/**
 * 
 * @author Sarah Bourgeois
 * @author Frederic Massart
 *
 */
public interface GeoLocationService {
    public GeoLocation requestGeoLocationFromIp(String ip) throws NudgeESConnectorException;
}

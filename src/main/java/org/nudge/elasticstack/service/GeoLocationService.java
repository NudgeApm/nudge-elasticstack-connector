package org.nudge.elasticstack.service;

import org.nudge.elasticstack.bean.GeoLocation;

import java.io.IOException;

/**
 * Created by Fred on 12/08/2016.
 */
public interface GeoLocationService {

    public GeoLocation requestGeoLocationFromIp(String ip) throws IOException;
}

package org.nudge.elasticstack.service.impl;

import org.apache.commons.lang.StringUtils;
import org.nudge.elasticstack.connection.NudgeApiConnection;
import org.nudge.elasticstack.context.nudge.filter.bean.Service;
import org.nudge.elasticstack.exception.NudgeApiException;
import org.nudge.elasticstack.service.NudgeAPIService;

import java.io.IOException;
import java.util.List;

/**
 * Created by fred on 13/07/17.
 */
public class NudgeAPIServiceImpl implements NudgeAPIService {

    NudgeApiConnection apiConnection;

    public NudgeAPIServiceImpl(NudgeApiConnection apiConnection) {
        this.apiConnection = apiConnection;
    }

    @Override
    public String retrieveConfiguredHostName(String appId, String host) throws NudgeApiException {
        List<Service> services;
        try {
            services = apiConnection.requestServices(appId);
        } catch (IOException ioe) {
            throw new NudgeApiException("Error while querying the service resource from Nudge API", ioe);
        }

        for (Service service : services) {
            // return the first one found
            if (host.equals(service.getHost()) && StringUtils.isNotBlank(service.getName())) {
                return service.getName();
            }
        }

        // default case
        return host;
    }
}

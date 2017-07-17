package org.nudge.elasticstack.service.impl;

import org.apache.commons.lang.StringUtils;
import org.nudge.elasticstack.connection.NudgeApiConnection;
import org.nudge.elasticstack.context.nudge.api.bean.App;
import org.nudge.elasticstack.context.nudge.api.bean.Service;
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
    public String retrieveAppName(String appId) throws NudgeApiException {
        App app;
        try {
             app = apiConnection.requestApp(appId);
        } catch (IOException ioe) {
            throw new NudgeApiException("Error while querying the App resource from Nudge API", ioe);
        }
        return app.getName();
    }

    @Override
    public String retrieveConfiguredHostName(String appId, String host) throws NudgeApiException {
        List<Service> services;
        try {
            services = apiConnection.requestServices(appId);
        } catch (IOException ioe) {
            throw new NudgeApiException("Error while querying the Service resource from Nudge API", ioe);
        }

        for (Service service : services) {
            // return the first one found
            if (host.equals(service.getHost()) && StringUtils.isNotBlank(service.getName())) {
                return service.getName();
            }
        }
        return host;
    }
}

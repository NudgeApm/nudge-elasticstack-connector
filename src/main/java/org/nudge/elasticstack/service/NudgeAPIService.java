package org.nudge.elasticstack.service;

import org.nudge.elasticstack.exception.NudgeApiException;


/**
 * Service interface for interact with the Nudge API.
 */
public interface NudgeAPIService {

    /**
     * Retrieve the configured name in Nudge of a host.
     *
     * @param appId the application id associated to the research
     * @param host the host searched
     * @return the configured name in Nudge if it is different from the given host
     */
    public String retrieveConfiguredHostName(String appId, String host) throws NudgeApiException;
}

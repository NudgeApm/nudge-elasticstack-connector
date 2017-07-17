package org.nudge.elasticstack.service;

import org.nudge.elasticstack.exception.NudgeApiException;


/**
 * Service interface for interact with the Nudge API.
 */
public interface NudgeAPIService {

    /**
     * Retrieve the name of the app configured in Nudge
     *
     * @param appId the app api key
     * @return the name
     * @throws NudgeApiException thrown for an error with the Nudge api
     */
    String retrieveAppName(String appId) throws NudgeApiException;

    /**
     * Retrieve the configured name in Nudge of a host.
     *
     * @param appId the application id associated to the research
     * @param host the host searched
     * @return the configured name in Nudge if it is different from the given host
     * @throws NudgeApiException thrown for an error with the Nudge api
     */
    public String retrieveConfiguredHostName(String appId, String host) throws NudgeApiException;
}

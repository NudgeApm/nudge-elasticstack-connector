package org.nudge.elasticstack.exception;

/**
 * Exception related to interaction with the Nudge API.
 */
public class NudgeApiException extends Exception {

    public NudgeApiException() {
        super();
    }

    public NudgeApiException(String message) {
        super(message);
    }

    public NudgeApiException(String message, Throwable cause) {
        super(message, cause);
    }
}

package org.nudge.elasticstack.exception;

@SuppressWarnings("serial")
public class UnsupportedElasticStackException extends RuntimeException {

	public UnsupportedElasticStackException(String message) {
		super(message);
	}

	public UnsupportedElasticStackException(String message, Throwable cause) {
		super(message, cause);
	}
}

package org.nudge.elasticstack;

@SuppressWarnings("serial")
public class NudgeESConnectorException extends Exception {

	public NudgeESConnectorException(String m) {
		super(m);
	}

	public NudgeESConnectorException(String m, Throwable t) {
		super(m, t);
	}
}

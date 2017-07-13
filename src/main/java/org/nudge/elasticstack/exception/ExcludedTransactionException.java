package org.nudge.elasticstack.exception;

/**
 * Thrown when a transaction must be excluded according to the configuration in Nudge.
 */
public class ExcludedTransactionException extends Exception {

    public ExcludedTransactionException() {
        super();
    }
}

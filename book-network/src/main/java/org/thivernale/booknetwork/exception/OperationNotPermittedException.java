package org.thivernale.booknetwork.exception;

public class OperationNotPermittedException extends RuntimeException {
    public OperationNotPermittedException(String message) {
        super(message);
    }
}

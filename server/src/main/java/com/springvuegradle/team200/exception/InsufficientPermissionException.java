package com.springvuegradle.team200.exception;

public class InsufficientPermissionException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Insufficient permission to do this action";

    public InsufficientPermissionException() {
        super(DEFAULT_MESSAGE);
    }
}

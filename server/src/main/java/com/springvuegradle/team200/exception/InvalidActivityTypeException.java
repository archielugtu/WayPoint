package com.springvuegradle.team200.exception;

public class InvalidActivityTypeException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "Invalid activity type: ";

    public InvalidActivityTypeException(String activityType) {
        super(DEFAULT_MESSAGE + activityType);
    }
}

package com.springvuegradle.team200.exception;

public class UnableToSaveImageException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "Unable to save image for activity with ID: ";

    public UnableToSaveImageException(Long activityId) {
        super(DEFAULT_MESSAGE + activityId);
    }
}

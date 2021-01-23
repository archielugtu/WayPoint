package com.springvuegradle.team200.exception;

public class ActivityNotFoundException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "Unable to find activity: ";
    private static final String DEFAULT_MESSAGE_ID = "Unable to find activity: ";

    public ActivityNotFoundException(String activityName) {
        super(DEFAULT_MESSAGE + "\"" + activityName + "\"");
    }

    public ActivityNotFoundException(Long activityId) {
        super(DEFAULT_MESSAGE_ID + activityId.toString());
    }
}

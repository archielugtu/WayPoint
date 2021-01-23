package com.springvuegradle.team200.exception;

public class HeroImageNotFoundException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "Unable to find hero image for activity ID: ";

    public HeroImageNotFoundException(Long activityId) {
        super(DEFAULT_MESSAGE + activityId);
    }
}

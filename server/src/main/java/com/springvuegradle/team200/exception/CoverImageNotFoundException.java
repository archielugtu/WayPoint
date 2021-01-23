package com.springvuegradle.team200.exception;

public class CoverImageNotFoundException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "Unable to find cover image for user ID: ";

    public CoverImageNotFoundException(Long userId) {
        super(DEFAULT_MESSAGE + userId);
    }
}

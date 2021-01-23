package com.springvuegradle.team200.exception;

public class UserNotFoundException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "Unable to find user with ID: ";

    public UserNotFoundException(String userId) {
        super(DEFAULT_MESSAGE + userId);
    }

    public UserNotFoundException(Long userId) {
        super(DEFAULT_MESSAGE + userId.toString());
    }
}

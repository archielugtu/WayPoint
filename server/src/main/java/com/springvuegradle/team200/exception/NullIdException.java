package com.springvuegradle.team200.exception;

public class NullIdException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "Request contains NULL id";

    public NullIdException() {
        super(DEFAULT_MESSAGE);
    }
}

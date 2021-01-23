package com.springvuegradle.team200.exception;

public class InvalidNumberOfPrimaryPhotosException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "Request does not contain primary photo";

    public InvalidNumberOfPrimaryPhotosException() {
        super();
    }
}

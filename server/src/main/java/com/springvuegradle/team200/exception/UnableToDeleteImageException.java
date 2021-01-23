package com.springvuegradle.team200.exception;

public class UnableToDeleteImageException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "Unable to delete image: ";

    public UnableToDeleteImageException(String imageName) {
        super(DEFAULT_MESSAGE + imageName);
    }
}

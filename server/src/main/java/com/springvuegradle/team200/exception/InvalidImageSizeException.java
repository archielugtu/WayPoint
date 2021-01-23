package com.springvuegradle.team200.exception;

public class InvalidImageSizeException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "Image does not meet size: ";

    public InvalidImageSizeException(int size) {
        super(DEFAULT_MESSAGE + size + "KB");
    }
}

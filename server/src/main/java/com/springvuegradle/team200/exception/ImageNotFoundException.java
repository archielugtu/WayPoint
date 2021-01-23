package com.springvuegradle.team200.exception;

public class ImageNotFoundException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "Unable to find image with ID: ";

    public ImageNotFoundException(Long photoId) {
        super(DEFAULT_MESSAGE + photoId);
    }
}

package com.springvuegradle.team200.exception;

import org.springframework.http.MediaType;

public class UnsupportedImageTypeException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "Image type is not supported: ";

    public UnsupportedImageTypeException(MediaType type) {
        super(DEFAULT_MESSAGE + type);
    }
}

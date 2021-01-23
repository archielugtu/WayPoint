package com.springvuegradle.team200.exception;

public class PhotosLimitExceededException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "User can only have 2 images";

    public PhotosLimitExceededException() {
        super();
    }
}

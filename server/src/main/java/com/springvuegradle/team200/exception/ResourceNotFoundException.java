package com.springvuegradle.team200.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resource) {
        super("Resource not found: " + resource);
    }
}

package com.springvuegradle.team200.exception;

public class ResourceUnreadableException extends RuntimeException {

    public ResourceUnreadableException(String resource) {
        super("Resource is not readable: " + resource);
    }
}

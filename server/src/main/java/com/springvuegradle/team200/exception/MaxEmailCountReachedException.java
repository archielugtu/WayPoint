package com.springvuegradle.team200.exception;

public class MaxEmailCountReachedException extends RuntimeException {
    public MaxEmailCountReachedException() {
        super("Max email count reached, cannot have more than 5");
    }
}

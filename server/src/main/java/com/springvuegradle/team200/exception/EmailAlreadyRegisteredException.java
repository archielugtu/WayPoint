package com.springvuegradle.team200.exception;

public class EmailAlreadyRegisteredException extends RuntimeException {

    public EmailAlreadyRegisteredException(String email) {
        super("Email already registered with the service: " + email);
    }

}

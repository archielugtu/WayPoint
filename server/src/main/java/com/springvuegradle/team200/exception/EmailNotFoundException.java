package com.springvuegradle.team200.exception;

public class EmailNotFoundException extends RuntimeException {

    public EmailNotFoundException(String email) {
        super("Email does not exist in the database " + email);
    }

}

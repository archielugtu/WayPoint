package com.springvuegradle.team200.exception;

public class EmailRegisteredWithAnotherUserException extends RuntimeException {

    public EmailRegisteredWithAnotherUserException(String email) {
        super("Email already registered: " + email);
    }

}

package com.springvuegradle.team200.exception;

public class InvalidAnswerException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Invalid answer format: ";

    public InvalidAnswerException(String answer) {
        super(DEFAULT_MESSAGE + answer);
    }
}

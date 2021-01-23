package com.springvuegradle.team200.exception;

public class InvalidOutcomeQuestionException extends RuntimeException {

    private static final String DEFAULT_MESSAGE_ID = "Invalid Id for outcome question: ";

    public InvalidOutcomeQuestionException(Long id) {
        super(DEFAULT_MESSAGE_ID + id);
    }
}

package com.springvuegradle.team200.exception;

public class OutcomeQuestionNotFoundException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "Unable to find Outcome: ";
    private static final String DEFAULT_MESSAGE_ID = "Unable to find Outcome: ";

    public OutcomeQuestionNotFoundException(String outcomeName) {
        super(DEFAULT_MESSAGE + "\"" + outcomeName + "\"");
    }

    public OutcomeQuestionNotFoundException(Long outcomeId) {
        super(DEFAULT_MESSAGE_ID + outcomeId.toString());
    }
}

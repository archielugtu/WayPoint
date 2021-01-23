package com.springvuegradle.team200.exception;

public class OutcomeMultichoiceOptionNotFoundException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "Unable to find Outcome Multichoice option: ";
    private static final String INVALID_LENGTH_MESSAGE = "Multichoice answers must have at least 2 options";
    private static final String DEFAULT_MESSAGE_ID = "Unable to find Outcome Multichoice option: ";

    public OutcomeMultichoiceOptionNotFoundException() {
        super(INVALID_LENGTH_MESSAGE);
    }

    public OutcomeMultichoiceOptionNotFoundException(String optionName) {
        super(DEFAULT_MESSAGE + "\"" + optionName + "\"");
    }

    public OutcomeMultichoiceOptionNotFoundException(Long optionId) {
        super(DEFAULT_MESSAGE_ID + optionId.toString());
    }
}

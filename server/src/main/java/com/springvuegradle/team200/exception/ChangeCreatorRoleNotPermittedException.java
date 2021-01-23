package com.springvuegradle.team200.exception;

public class ChangeCreatorRoleNotPermittedException extends RuntimeException {

    private static final String DEFAULT_MESSAGE_ID = "Not allowed to change creator's role in activity ID: ";

    public ChangeCreatorRoleNotPermittedException(Long activityId) {
        super(DEFAULT_MESSAGE_ID + activityId.toString());
    }
}

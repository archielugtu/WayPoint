package com.springvuegradle.team200.validator;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

public class ValidationError {


    private static final String ERROR = "ERROR";
    private final String errorMessage;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> errors = new ArrayList<>();

    public ValidationError(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void addValidationError(String error) {
        errors.add(error);
    }

    // These getters are required for them to be present in the response JSON
    // * Do _not_ remove *

    public List<String> getErrors() {
        return errors;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getStatus() {
        return ERROR;
    }
}

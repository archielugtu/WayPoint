package com.springvuegradle.team200.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;

public class ValidationErrorBuilder {

    private ValidationErrorBuilder() {
        // left empty intentionally
    }

    public static ValidationError fromBindingErrors(Errors errors) {
        ValidationError error = new ValidationError("Validation failed. " + errors.getErrorCount() + " error(s)");
        for (ObjectError objectError : errors.getAllErrors()) {
            error.addValidationError(objectError.getDefaultMessage());
        }
        return error;
    }

    public static ValidationError fromBindingErrors(String error) {
        ValidationError err = new ValidationError("Validation failed. " + 1 + " error(s)");
        err.addValidationError(error);
        return err;
    }
}

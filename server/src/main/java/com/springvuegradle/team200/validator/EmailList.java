package com.springvuegradle.team200.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Annotation to handle validating a List of email String
 * <p>
 * Usage: @EmailList on List of String field
 */
@Constraint(validatedBy = {EmailListValidator.class})
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
public @interface EmailList {
    String message() default "Invalid Email Address";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

package com.springvuegradle.team200.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Annotation to handle date validation
 * <p>
 * lowerBound and upperBound indicates the upper and lower bound of the date in years
 * dates between upper and lower are considered valid
 * <p>
 * Usage: @Between(lowerbound = 1, upperbound = 10, required = true) on Date object
 */
@Constraint(validatedBy = {BetweenDateValidator.class})
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
public @interface Between {
    String message() default "Date is invalid";

    /**
     * Upper bound in years
     */
    int upperBound() default 0;

    /**
     * Lower bound in years
     */
    int lowerBound() default 0;

    /**
     * Whether the field is required or not
     */
    boolean required() default true;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

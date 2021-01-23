package com.springvuegradle.team200.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Calendar;
import java.util.Date;

/**
 * Implements validation logic for @Between annotation
 * <p>
 * valid if date is between lowerbound and upperbound
 */
public class BetweenDateValidator implements ConstraintValidator<Between, Date> {

    private int upperBound;
    private int lowerBound;
    private boolean required;

    @Override
    public void initialize(Between constraintAnnotation) {
        this.upperBound = constraintAnnotation.upperBound();
        this.lowerBound = constraintAnnotation.lowerBound();
        this.required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(Date value, ConstraintValidatorContext context) {
        if (value == null) {
            return !required;
        }

        boolean isValid;

        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.YEAR, upperBound);
        Date upperBoundDate = calendar.getTime();
        isValid = value.before(upperBoundDate);

        calendar.clear();

        calendar.add(Calendar.YEAR, lowerBound);
        Date lowerBoundDate = calendar.getTime();
        isValid &= value.after(lowerBoundDate);

        return isValid;
    }
}

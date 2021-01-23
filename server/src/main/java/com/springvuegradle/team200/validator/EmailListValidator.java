package com.springvuegradle.team200.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

public class EmailListValidator implements ConstraintValidator<EmailList, List<String>> {
    private final Pattern emailPattern = compile("^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$");

    @Override
    public void initialize(final EmailList constraintAnnotation) {
        //empty constructor
    }

    @Override
    public boolean isValid(List<String> value, ConstraintValidatorContext context) {
        // We can have empty list of emails
        if (value.isEmpty()) {
            return true;
        }

        // else check if all of the emails are not blank and matches the Regex pattern above
        return value.stream()
                .filter(e -> !e.isBlank())
                .filter(e -> emailPattern.matcher(e).matches())
                .count() == value.size();
    }
}

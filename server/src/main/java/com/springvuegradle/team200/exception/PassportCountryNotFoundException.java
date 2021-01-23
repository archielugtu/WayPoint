package com.springvuegradle.team200.exception;

public class PassportCountryNotFoundException extends RuntimeException {

    public PassportCountryNotFoundException(String country) {
        super("Unable to find country: " + country);
    }
}

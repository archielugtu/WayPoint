package com.springvuegradle.team200.model;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum for storing the data type associated with an ActivityResult
 */
public enum OutcomeDataType {
    STRING,
    INTEGER,
    FLOAT,
    BOOLEAN,
    DATE,
    TIME,
    BOOLEAN_ARRAY;

    @JsonValue
    public String getText() {
        return this.toString();
    }

    @Override
    public String toString() { return name().toLowerCase(); }
}

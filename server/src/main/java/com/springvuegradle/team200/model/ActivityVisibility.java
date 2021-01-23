package com.springvuegradle.team200.model;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum representing the visibility of an activity.
 * <p>
 * An activity may be public, restricted (visible to a select few),
 * or private (visible only to the creator).
 */
public enum ActivityVisibility {
    PUBLIC,
    RESTRICTED,
    PRIVATE;

    /**
     * The @JsonValue annotation handles deserialising from
     * the string representation to its enum value
     */
    @JsonValue
    public String getInText() {
        return this.toString();
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}

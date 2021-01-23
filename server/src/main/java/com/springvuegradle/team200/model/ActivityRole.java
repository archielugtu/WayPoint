package com.springvuegradle.team200.model;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum representing the role a user has in relation to an activity.
 * <p>
 * A user may be a follower, participant, organiser, or creator.
 * Each role may have different permissions associated with them,
 * hence the ordinal value may be used for rank comparison.
 */
public enum ActivityRole {
    NONE,
    FOLLOWER,
    PARTICIPANT,
    ORGANISER,
    CREATOR;

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

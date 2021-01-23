package com.springvuegradle.team200.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.springvuegradle.team200.model.ActivityRole;

public class EditActivityRoleRequest {

    @JsonProperty("role")
    // Uses @JsonValue annotation in the Enum class to handle deserialising
    private ActivityRole role;

    /**
     * Constructor
     *
     * @param role The user's activity role as a string,
     *             should be one of "follower", "participant", "organiser", "creator"
     */
    public EditActivityRoleRequest(ActivityRole role) {
        this.role = role;
    }

    public EditActivityRoleRequest() {

    }

    public ActivityRole getRole() {
        return role;
    }

    public void setRole(ActivityRole value) {
        role = value;
    }
}

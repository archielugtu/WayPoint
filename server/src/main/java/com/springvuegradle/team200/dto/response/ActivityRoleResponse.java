package com.springvuegradle.team200.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.springvuegradle.team200.model.ActivityRole;

public class ActivityRoleResponse {

    @JsonProperty("role")
    private String role;

    /**
     * Constructor
     *
     * @param role The role of the user associated with an activity
     */
    public ActivityRoleResponse(ActivityRole role) {
        this.role = role.toString();
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}


package com.springvuegradle.team200.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.springvuegradle.team200.model.ActivityRole;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 * A class to represent the activities accessors
 */
public class ActivityAccessorRequest {

    @JsonProperty("email")
    private String email;

    @JsonProperty("role")
    @Enumerated(EnumType.STRING)
    private ActivityRole role;

    public ActivityAccessorRequest(String email, ActivityRole role) {
        this.email = email;
        this.role = role;
    }

    public ActivityAccessorRequest() {}

    public ActivityRole getRole() {
        return role;
    }

    public void setRole(ActivityRole role) {
        this.role = role;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

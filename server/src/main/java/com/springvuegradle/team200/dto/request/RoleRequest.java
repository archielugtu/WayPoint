package com.springvuegradle.team200.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;

public class RoleRequest {

    @NotBlank
    @JsonProperty("role")
    private String role;

    /**
     * Constructor
     *
     * @param role The string role for the user
     */
    public RoleRequest(String role) {
        this.role = role;
    }

    /**
     * This constructor is not used in the controller but useful for test
     */
    public RoleRequest() {
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}

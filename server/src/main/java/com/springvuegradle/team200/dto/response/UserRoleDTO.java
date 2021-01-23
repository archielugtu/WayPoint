package com.springvuegradle.team200.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigInteger;
import java.util.Map;

public class UserRoleDTO {

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("email")
    private String email;

    @JsonProperty("role")
    private String role;

    public UserRoleDTO() {
        //left empty intentionally

    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public static UserRoleDTO of(Map<String, Object> dbResult) {
        UserRoleDTO dto = new UserRoleDTO();
        dto.setFirstName((String) dbResult.get("firstName"));
        dto.setLastName((String) dbResult.get("lastName"));
        dto.setEmail((String) dbResult.get("address"));
        dto.setRole((String) dbResult.get("role"));
        dto.setUserId(((BigInteger) dbResult.get("userId")).longValue());
        return dto;
    }
}

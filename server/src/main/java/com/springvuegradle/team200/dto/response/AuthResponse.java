package com.springvuegradle.team200.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.springvuegradle.team200.model.User;

public class AuthResponse {

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("is_admin")
    private Boolean isAdmin;

    @JsonProperty("is_global_admin")
    private Boolean isGlobalAdmin;

    public static AuthResponse of(User user) {
        AuthResponse authResponse = new AuthResponse();
        authResponse.setUserId(user.getId());
        authResponse.setAdmin(user.getIsAdmin());
        authResponse.setGlobalAdmin(user.getIsGlobalAdmin());

        return authResponse;
    }

    public Long getUserId() {
        return userId;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public Boolean getGlobalAdmin() {
        return isGlobalAdmin;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public void setGlobalAdmin(Boolean globalAdmin) {
        isGlobalAdmin = globalAdmin;
    }
}

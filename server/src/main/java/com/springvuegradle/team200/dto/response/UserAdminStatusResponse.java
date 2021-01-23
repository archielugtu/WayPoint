package com.springvuegradle.team200.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.springvuegradle.team200.model.User;

public class UserAdminStatusResponse {

    @JsonProperty("primary_email")
    private String primaryEmail;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("is_admin")
    private boolean isAdmin;

    public UserAdminStatusResponse() {
        //empty constructor
    }

    public static UserAdminStatusResponse of(User user) {
        UserAdminStatusResponse response = new UserAdminStatusResponse();
        response.setUserId(user.getId());
        response.setAdmin(user.getIsAdmin());
        response.setPrimaryEmail(user.getPrimaryEmail().getAddress());
        return response;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public String getPrimaryEmail() {
        return primaryEmail;
    }

    public void setPrimaryEmail(String primaryEmail) {
        this.primaryEmail = primaryEmail;
    }
}

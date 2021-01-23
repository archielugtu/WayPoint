package com.springvuegradle.team200.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;

public class PasswordRequest {
    @NotBlank(message = "New password cannot be blank")
    @JsonProperty("new_password")
    private String newPassword;

    @NotBlank(message = "New password cannot be blank")
    @JsonProperty("repeat_password")
    private String confirmPassword;

    @JsonProperty("old_password")
    private String password;

    /**
     * Constructor
     *
     * @param password        The user's old password
     * @param newPassword     The new password
     * @param confirmPassword The new password, again, for confirmation
     */
    public PasswordRequest(String password, String newPassword, String confirmPassword) {
        this.newPassword = newPassword;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public String getPassword() {
        return password;
    }
}

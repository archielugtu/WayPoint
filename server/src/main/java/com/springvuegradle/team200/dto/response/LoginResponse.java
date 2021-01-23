package com.springvuegradle.team200.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.web.bind.annotation.ResponseBody;

@ResponseBody
public class LoginResponse extends GenericResponse {
    @JsonProperty("urlRedirect")
    private String urlRedirect;

    @JsonProperty("userId")
    private Long userId;

    @JsonProperty("isAdmin")
    private boolean isAdmin;

    @JsonProperty("isGlobalAdmin")
    private boolean isGlobalAdmin;

    /**
     * Constructor intended for bad login attempts
     *
     * @param status The String status message
     */
    public LoginResponse(String status) {
        this(status, "", 0L, false, false);
    }

    /**
     * Constructor
     *
     * @param status      The String status message
     * @param urlRedirect The URL name to redirect to
     * @param userId      The user's ID
     * @param isAdmin     Whether the user logging in is an admin - true if admin, false if otherwise
     */
    public LoginResponse(String status, String urlRedirect, Long userId, boolean isGlobalAdmin, boolean isAdmin) {
        super(status);
        this.isAdmin = isAdmin;
        this.urlRedirect = urlRedirect;
        this.userId = userId;
        this.isGlobalAdmin = isGlobalAdmin;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUrlRedirect() {
        return urlRedirect;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public boolean isGlobalAdmin() {
        return isGlobalAdmin;
    }
}

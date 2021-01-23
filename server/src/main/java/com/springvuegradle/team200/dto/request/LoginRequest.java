package com.springvuegradle.team200.dto.request;

public class LoginRequest {
    private String email;
    private String password;

    /**
     * Constructor
     *
     * @param email    The String email of an attempted login
     * @param password The String password of an attempted login
     */
    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}

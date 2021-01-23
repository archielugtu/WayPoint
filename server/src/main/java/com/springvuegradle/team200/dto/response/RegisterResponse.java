package com.springvuegradle.team200.dto.response;

import org.springframework.web.bind.annotation.ResponseBody;

@ResponseBody
public class RegisterResponse {

    private final String status;

    private final String id;

    /**
     * Constructor
     *
     * @param status The String status
     * @param id     The ID of the new user
     */
    public RegisterResponse(String status, String id) {
        this.status = status;
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public String getId() {
        return id;
    }
}

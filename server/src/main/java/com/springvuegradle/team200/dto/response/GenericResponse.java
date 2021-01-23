package com.springvuegradle.team200.dto.response;

public abstract class GenericResponse {
    private String status;

    public GenericResponse(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

package com.springvuegradle.team200.dto.request;

import javax.validation.constraints.Min;

public class SearchUserRequest {

    private String firstname = "";

    private String lastname = "";

    private String email = "";

    @Min(value = 0, message = "Page cannot be a negative number")
    private int page;

    private String activities = "";

    private String method = "";

    public SearchUserRequest() {
        //empty constructor
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getActivities() {
        return activities;
    }

    public void setActivities(String activities) {
        this.activities = activities;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    @Override
    public String toString() {
        return "SearchUserRequest{" +
                "firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", email='" + email + '\'' +
                ", page=" + page +
                ", activities='" + activities + '\'' +
                ", method='" + method + '\'' +
                '}';
    }
}

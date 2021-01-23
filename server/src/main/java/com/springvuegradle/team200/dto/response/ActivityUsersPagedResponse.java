package com.springvuegradle.team200.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ActivityUsersPagedResponse {

    @JsonProperty("total_pages")
    private int totalPages = 0;

    @JsonProperty("total_users")
    private int totalUsers = 0;

    public ActivityUsersPagedResponse() {
        //left empty intentionally
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(int totalUsers) {
        this.totalUsers = totalUsers;
    }
}

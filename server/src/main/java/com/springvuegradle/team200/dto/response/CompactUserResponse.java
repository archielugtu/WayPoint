package com.springvuegradle.team200.dto.response;

import com.springvuegradle.team200.model.User;

public class CompactUserResponse {

    private Long userId;

    private String name;

    public CompactUserResponse() {
        //left empty intentionally
    }

    public static CompactUserResponse of(User user) {
        CompactUserResponse compactUserResponse = new CompactUserResponse();
        compactUserResponse.setName(user.getFirstName() + ' ' + user.getLastName());
        compactUserResponse.setUserId(user.getId());
        return compactUserResponse;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

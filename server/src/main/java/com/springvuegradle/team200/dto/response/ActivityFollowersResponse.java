package com.springvuegradle.team200.dto.response;

import java.util.ArrayList;
import java.util.List;

public class ActivityFollowersResponse extends ActivityUsersPagedResponse {

    List<CompactUserResponse> followers = new ArrayList<>();


    public ActivityFollowersResponse() {
        //left empty intentionally
    }

    public List<CompactUserResponse> getFollowers() {
        return followers;
    }

    public void setFollowers(List<CompactUserResponse> followers) {
        this.followers = followers;
    }
}

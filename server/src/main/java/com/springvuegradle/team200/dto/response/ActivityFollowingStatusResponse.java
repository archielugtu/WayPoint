package com.springvuegradle.team200.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ActivityFollowingStatusResponse {

    @JsonProperty("subscribed")
    private boolean subscribed;

    /**
     * Constructor
     *
     * @param subscribed The user's following/subscribed status, as a boolean
     */
    public ActivityFollowingStatusResponse(boolean subscribed) {
        this.subscribed = subscribed;
    }

    public boolean getSubscribed() {
        return subscribed;
    }

    public void setSubscribed(boolean subscribed) {
        this.subscribed = subscribed;
    }
}

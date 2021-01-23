package com.springvuegradle.team200.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ActivityUsersCountResponse {

    @JsonProperty("total_followers")
    private int totalFollowers;

    @JsonProperty("total_organisers")
    private int totalOrganisers;

    @JsonProperty("total_participants")
    private int totalParticipants;

    @JsonProperty("visibility")
    private String visibility;

    public ActivityUsersCountResponse() {
        //left empty intentionally
    }

    public int getTotalFollowers() {
        return totalFollowers;
    }

    public void setTotalFollowers(int totalFollowers) {
        this.totalFollowers = totalFollowers;
    }

    public int getTotalOrganisers() {
        return totalOrganisers;
    }

    public void setTotalOrganisers(int totalOrganisers) {
        this.totalOrganisers = totalOrganisers;
    }

    public int getTotalParticipants() {
        return totalParticipants;
    }

    public void setTotalParticipants(int totalParticipants) {
        this.totalParticipants = totalParticipants;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }
}

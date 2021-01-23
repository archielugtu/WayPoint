package com.springvuegradle.team200.dto.response;

import java.util.ArrayList;
import java.util.List;

public class ActivityOrganisersResponse extends ActivityUsersPagedResponse {

    List<CompactUserResponse> organisers = new ArrayList<>();

    public ActivityOrganisersResponse() {
        //left empty intentionally
    }

    public List<CompactUserResponse> getOrganisers() {
        return organisers;
    }

    public void setOrganisers(List<CompactUserResponse> organisers) {
        this.organisers = organisers;
    }
}

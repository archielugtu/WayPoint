package com.springvuegradle.team200.dto.response;

import java.util.ArrayList;
import java.util.List;

public class ActivityParticipantsResponse extends ActivityUsersPagedResponse {

    List<CompactUserResponse> participants = new ArrayList<>();

    public ActivityParticipantsResponse() {
        //left empty intentionally
    }

    public List<CompactUserResponse> getParticipants() {
        return participants;
    }

    public void setParticipants(List<CompactUserResponse> participants) {
        this.participants = participants;
    }
}

package com.springvuegradle.team200.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@ResponseBody
public class ActivityTypesResponse {
    @JsonProperty("activities")
    private List<String> activities;

    /**
     * Constructor
     *
     * @param activityTypes A List of String activity types
     */
    public ActivityTypesResponse(List<String> activityTypes) {
        this.activities = activityTypes;
    }
}

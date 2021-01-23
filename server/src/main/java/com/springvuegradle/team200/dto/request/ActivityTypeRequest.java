package com.springvuegradle.team200.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class ActivityTypeRequest {

    @JsonProperty("activities")
    @NotNull(message = "Activity types cannot be null.")
    private List<String> activities = new ArrayList<>();

    /**
     * Constructor for activity types
     *
     * @param activities A List of String activity types
     */
    public ActivityTypeRequest(List<String> activities) {
        this.activities = activities;
    }

    /**
     * Constructor
     */
    public ActivityTypeRequest() {
        //empty constructor
    }

    public List<String> getActivities() {
        return activities;
    }
}

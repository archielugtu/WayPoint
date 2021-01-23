package com.springvuegradle.team200.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.springvuegradle.team200.model.ActivityVisibility;

import javax.validation.constraints.NotNull;
import java.util.List;

public class ActivityVisibilityRequest {

    @JsonProperty("visibility")
    @NotNull(message = "Visibility cannot be blank")
    ActivityVisibility visibility = ActivityVisibility.PUBLIC;

    @JsonProperty("accessors")
    List<ActivityAccessorRequest> accessors;

    public ActivityVisibilityRequest(ActivityVisibility visibility, List<ActivityAccessorRequest> accessors) {
        this.visibility = visibility;
        this.accessors = accessors;
    }

    public ActivityVisibilityRequest() {}

    public ActivityVisibility getVisibility() {
        return visibility;
    }

    public void setVisibility(ActivityVisibility visibility) {
        this.visibility = visibility;
    }

    public List<ActivityAccessorRequest> getAccessors() {
        return accessors;
    }

    public void setAccessors(List<ActivityAccessorRequest> accessors) {
        this.accessors = accessors;
    }
}

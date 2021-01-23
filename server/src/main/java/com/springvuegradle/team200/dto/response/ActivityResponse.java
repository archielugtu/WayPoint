package com.springvuegradle.team200.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.springvuegradle.team200.model.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ActivityResponse {

    @JsonProperty("activity_id")
    private Long activityId;

    @JsonProperty("creator_id")
    private Long creatorId;

    @JsonProperty("creator_name")
    private String creatorName;

    @JsonProperty("description")
    private String description;

    @JsonProperty("activity_name")
    private String activityName;

    @JsonProperty("activity_type")
    private List<String> activityTypes;

    @JsonProperty("continous")
    private Boolean isContinuous;

    @JsonProperty("start_time")
    private Date startTime;

    @JsonProperty("end_time")
    private Date endTime;

    @JsonProperty("location")
    private Location location;

    @JsonProperty("hashtags")
    private List<String> hashtags;

    @JsonProperty("role")
    private ActivityRole role;

    @JsonProperty("visibility")
    private ActivityVisibility visibility;

    /**
     * Constructor
     *
     * @param activity The activity associated with this response
     */
    public ActivityResponse(Activity activity, int activityRole) {
        this.activityId = activity.getId();
        User creator = activity.getCreator();
        if (creator != null) {
            this.creatorId = creator.getId();
            this.creatorName = creator.getFirstName() + " " + creator.getLastName();
        }
        this.activityName = activity.getActivityName();
        this.description = activity.getDescription();
        this.activityTypes = activity.getActivityTypes().stream()
                .map(ActivityType::getType)
                .collect(Collectors.toList());
        this.isContinuous = activity.isContinuous();
        this.startTime = activity.getStartDate();
        this.endTime = activity.getEndDate();
        this.location = activity.getLocation();
        this.hashtags = activity.getHashtags().stream()
                .map(Hashtag::getName)
                .collect(Collectors.toList());
        this.role = ActivityRole.values()[activityRole];
        this.visibility = activity.getVisibility();
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public List<String> getActivityTypes() {
        return activityTypes;
    }

    public void setActivityTypes(List<String> activityTypes) {
        this.activityTypes = activityTypes;
    }

    public Boolean getContinuous() {
        return isContinuous;
    }

    public void setContinuous(Boolean continuous) {
        isContinuous = continuous;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public List<String> getHashtags() {
        return hashtags;
    }

    public void setHashtags(List<String> hashtags) {
        this.hashtags = hashtags;
    }

    public ActivityRole getRole() {
        return role;
    }

    public void setRole(ActivityRole role) {
        this.role = role;
    }

    public ActivityVisibility getVisibility() {
        return visibility;
    }

    public void setVisibility(ActivityVisibility visibility) {
        this.visibility = visibility;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}


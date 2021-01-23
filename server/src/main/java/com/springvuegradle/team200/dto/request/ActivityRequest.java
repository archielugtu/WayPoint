package com.springvuegradle.team200.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.springvuegradle.team200.model.ActivityVisibility;
import com.springvuegradle.team200.model.Location;
import com.springvuegradle.team200.model.OutcomeQuestion;
import com.springvuegradle.team200.validator.Between;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.*;

public class ActivityRequest {

    private static final int MAX_LOCATION_LENGTH = 200;
    private static final int MAX_DESCRIPTION_LENGTH = 600;
    private static final int MAX_HASHTAG_LENGTH = 30;
    private static final int MAX_ACTIVITY_NAME_LENGTH = 60;

    @NotBlank(message = "Activity name cannot be blank")
    @Size(max = MAX_ACTIVITY_NAME_LENGTH, message = "Activity name cannot be longer than "
            + MAX_ACTIVITY_NAME_LENGTH + " characters.")
    @JsonProperty("activity_name")
    private String activityName;

    @Size(max = MAX_DESCRIPTION_LENGTH, message = "Activity description cannot be longer than "
            + MAX_DESCRIPTION_LENGTH + " characters")
    @JsonProperty("description")
    private String description;

    @NotNull(message = "'continuous' (time range) is a required field")
    @JsonProperty("continuous")
    private boolean isContinuous = true;

    @Between(upperBound = 3, required = false, message = "Start date is invalid")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    @JsonProperty("start_time")
    private Date startDate;

    @Between(upperBound = 3, required = false, message = "End date is invalid")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    @JsonProperty("end_time")
    private Date endDate;

    @JsonProperty("location")
    @NotNull(message = "location is required for activities")
    private Location location;

    @Size(min = 1, message = "Activities needs to have an associated activity types")
    @NotNull(message = "Activity types is a required field")
    @JsonProperty("activity_type")
    private List<String> activityTypes = new ArrayList<>();

    @Size(max = MAX_HASHTAG_LENGTH, message = "Activity can only have up to "
            + MAX_HASHTAG_LENGTH + " hashtags")
    @JsonProperty("hashtags")
    private List<String> hashtags;

    @NotNull(message = "Activity visibility cannot be blank")
    @JsonProperty("visibility")
    private ActivityVisibility visibility = ActivityVisibility.PUBLIC;

    @JsonProperty("specifications")
    private Set<OutcomeQuestion> outcomeQuestions;

    public ActivityRequest() {
    }

    /**
     * Constructor
     *
     * @param activityName  The name of the Activity
     * @param description   The description of the activity
     * @param activityTypes A List of String activity types
     * @param isContinuous  Whether the activity is continuous - true if continuous, false if otherwise
     * @param startDate     The start Date of the activity if it is not a continuous activity
     * @param endDate       The end Date of the activity if it is not a continuous activity
     * @param location      The location of the activity
     */
    @JsonCreator
    public ActivityRequest(String activityName, String description, List<String> activityTypes, boolean isContinuous,
                           Date startDate, Date endDate, Location location, List<String> hashtags, ActivityVisibility visibilty) {
        this.activityName = activityName;
        this.description = description;
        this.isContinuous = isContinuous;
        this.startDate = startDate;
        this.endDate = endDate;
        this.location = location;
        this.activityTypes = activityTypes;
        this.hashtags = hashtags;
        this.visibility = visibilty;
        this.outcomeQuestions = new HashSet<>();
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isContinuous() {
        return isContinuous;
    }

    public void setContinuous(boolean continuous) {
        isContinuous = continuous;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public List<String> getActivityTypes() {
        return activityTypes;
    }

    public void setActivityTypes(List<String> activityTypes) {
        this.activityTypes = activityTypes;
    }

    public List<String> getHashtags() {
        return hashtags;
    }

    public void setHashtags(List<String> hashtags) {
        this.hashtags = hashtags;
    }

    public Set<OutcomeQuestion> getOutcomeQuestions() {
        return outcomeQuestions;
    }

    public void setOutcomeQuestions(Set<OutcomeQuestion> outcomeQuestions) {
        this.outcomeQuestions = outcomeQuestions;
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

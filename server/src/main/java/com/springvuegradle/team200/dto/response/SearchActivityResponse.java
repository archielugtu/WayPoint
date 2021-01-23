package com.springvuegradle.team200.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.springvuegradle.team200.model.Activity;
import com.springvuegradle.team200.model.Location;

import java.util.Date;

public class SearchActivityResponse {
    @JsonProperty("activity_name")
    private String activityName;

    @JsonProperty("description")
    private String description;

    @JsonProperty("location")
    private Location location;

    @JsonProperty("start_date")
    private Date startDate;

    @JsonProperty("creation_date")
    private Date creationDate;

    @JsonProperty("id")
    private Long id;

    public static SearchActivityResponse of(Activity activity) {
        SearchActivityResponse searchActivityResponse = new SearchActivityResponse();
        searchActivityResponse.setActivityName(activity.getActivityName());
        searchActivityResponse.setId(activity.getId());
        searchActivityResponse.setDescription(activity.getDescription());
        searchActivityResponse.setLocation(activity.getLocation());
        searchActivityResponse.setStartDate(activity.getStartDate());
        searchActivityResponse.setCreationDate(activity.getCreationDate());
        return searchActivityResponse;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }
    
    public Long getId() {
        return id;
    }
     
    public void setId(Long id) {
        this.id = id;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}

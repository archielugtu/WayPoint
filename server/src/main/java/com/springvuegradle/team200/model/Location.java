package com.springvuegradle.team200.model;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;

/**
 * A class to represent the user's location
 */
@Embeddable
public class Location {

    /**
     * The String location place Id according to google maps
     * Always specified, even if latitude/longitude are used to pinpoint location
     */
    @Column(name = "location_place_id")
    @JsonProperty
    private String placeId;

    /**
     * Additional textual info to help users find the location
     */
    @Column(name = "description")
    @JsonProperty
    private String description;

    /**
     * Latitude of location
     * Only specified if the placeId is not sufficient for the location
     */
    @Column(name = "latitude", nullable = false)
    @JsonProperty
    private Float latitude;

    /**
     * Longitude of location
     * Only specified if the placeId is not sufficient for the location
     */
    @Column(name = "longitude", nullable = false)
    @JsonProperty
    private Float longitude;

    /**
     * Specifies whether the location is using a coorindate system, or using the coordinates derived from a placeId
     */
    @Column(name = "is_using_coordinates")
    @JsonProperty
    private Boolean isUsingCoordinates;

    /**
     * Default constructor
     */
    public Location() {
    }

    /**
     * Full constructor
     */
    public Location(String placeId, String description, Float latitude, Float longitude, Boolean isUsingCoordinates) {
        this.placeId = placeId;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isUsingCoordinates = isUsingCoordinates;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Float getLatitude() {
        return latitude;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    public Float getLongitude() {
        return longitude;
    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }

    public Boolean getUsingCoordinates() {
        return isUsingCoordinates;
    }

    public void setUsingCoordinates(Boolean usingCoordinates) {
        isUsingCoordinates = usingCoordinates;
    }
}

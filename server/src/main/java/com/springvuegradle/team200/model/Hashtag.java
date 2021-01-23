package com.springvuegradle.team200.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.Set;

/**
 * A class to represent hashtags used on activities
 */
@Entity
@Table(name = "hashtag")
public class Hashtag {

    /**
     * The hashtag's unique ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hashtag_id")
    private Long hashtagId;

    /**
     * Hashtag text. Should be formatted as a hashtag then lowercase string with underscore spacing
     */
    @Column(unique = true)
    private String name;

    /**
     * The {@link Activity} activities associated with this hashtag
     */
    @ManyToMany(mappedBy = "hashtags")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Set<Activity> activities;

    public Hashtag(String name) {
        this.name = name;
    }

    public Hashtag() {
    }

    public Long getHashtagId() {
        return hashtagId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Activity> getActivities() {
        return activities;
    }

    public void setActivities(Set<Activity> activities) {
        this.activities = activities;
    }
}
package com.springvuegradle.team200.model;

import javax.persistence.*;

/**
 * A class to represent the activity types that the user can have.
 */
@Entity
@Table(name = "app_activity_types")
public class ActivityType {

    /**
     * The LONG id of the activity type.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "activity_type_id")
    private long activityTypeId;

    /**
     * The activity type.
     */
    @Column(unique = true, name = "activity_type")
    private String type;

    /**
     * Default constructor
     */
    public ActivityType() {
    }

    public ActivityType(String type) {
        this.type = type;
    }

    /**
     * @return The activity type's unique LONG id
     */
    public long getActivityTypeId() {
        return activityTypeId;
    }

    /**
     * @return A string representing the name of the activity type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the name of a newly created activity type
     *
     * @param activityType The String name of the activity type
     */
    public void setType(String activityType) {
        this.type = activityType;
    }
}

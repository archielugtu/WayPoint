package com.springvuegradle.team200.model;

import com.fasterxml.jackson.annotation.JsonCreator;

import javax.persistence.*;
import java.util.*;

/**
 * A class to represent an activity, and store any associated details.
 */
@Entity
@Table(name = "activity")
public class Activity {

    /**
     * The activity's unique ID
     */
    @Id
    @GeneratedValue
    @Column(name = "activity_id")
    private Long id;

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "activity")
    private List<UserActivityHistory> histories;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User creator;

    @ManyToMany
    @JoinTable(
            name = "user_activity_organisers",
            joinColumns = @JoinColumn(name = "activity_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> organisers = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "user_activity_participation",
            joinColumns = @JoinColumn(name = "activity_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> participants;

    @ManyToMany
    @JoinTable(
            name = "user_activity_followers",
            joinColumns = @JoinColumn(name = "activity_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> followers;

    @ManyToMany
    @JoinTable(
            name = "user_activity_shared_users",
            joinColumns = @JoinColumn(name = "activity_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> sharedUsers;

    /**
     * Activity name
     */
    @Column(name = "activity_name")
    private String activityName;

    /**
     * Activity description
     */
    @Column(length = 600)
    private String description;

    /**
     * A list of {@link ActivityType} objects associated with the activity
     */
    @ManyToMany
    @JoinTable(
            name = "activity_activity_types",
            joinColumns = @JoinColumn(name = "activity_id"),
            inverseJoinColumns = @JoinColumn(name = "activity_type_id"))
    private List<ActivityType> activityTypes;

    /**
     * A boolean that indiciates whether the activity continuous.
     */
    @Column
    private boolean isContinuous;

    /**
     * Date type that indicates the start date of the activity.
     */
    @Column(name = "start_date")
    private Date startDate;

    /**
     * Date type that indicates the end date of the activity.
     */
    @Column(name = "end_date")
    private Date endDate;

    /**
     * Date type that indicates when the activity was made.
     */
    @Column(name = "creation_date")
    private Date creationDate;

    /**
     * The activity's {@link Location}
     */
    @Embedded
    @AttributeOverrides( {
            @AttributeOverride(name="locationPlaceId", column = @Column(name="location_place_id") ),
            @AttributeOverride(name="description", column = @Column(name="location_description") ),
            @AttributeOverride(name="latitude", column = @Column(name="location_latitude") ),
            @AttributeOverride(name="longitude", column = @Column(name="location_longitude") ),
    } )
    private Location location;

    @OneToMany(mappedBy = "activity", cascade = CascadeType.REMOVE)
    private List<Photo> images = new ArrayList<>();

    /**
     * Set of all hashtags for an activity
     */
    @ManyToMany
    @JoinTable(
            name = "activity_hashtags",
            joinColumns = @JoinColumn(name = "activity_id"),
            inverseJoinColumns = @JoinColumn(name = "hashtag_id"))
    private Set<Hashtag> hashtags;

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.REMOVE}, mappedBy = "activity")
    private Set<OutcomeQuestion> outcomeQuestions;

    @Column
    @Enumerated(EnumType.STRING)
    private ActivityVisibility visibility;

    /**
     * Empty constructor
     */
    public Activity() {
    }

    /**
     * Constructor
     *
     * @param name              The name of the activity
     * @param description       The activity's description
     * @param location          The location for the activity according to google maps api
     * @param isContinuous      Whether the activity is continuous - true if continuous, false if otherwise
     * @param startDate         The start Date for the activity, if it is not a continuous activity
     * @param endDate           The end Date for the activity, if it is not a continuous activity
     * @param creationDate      The Date the activity was created on
     */
    @JsonCreator
    public Activity(String name, String description, Location location, boolean isContinuous, Date startDate,
                    Date endDate, Date creationDate, ActivityVisibility visibility) {
        this.activityName = name;
        this.description = description;
        this.location = location;
        this.isContinuous = isContinuous;
        this.startDate = startDate;
        this.endDate = endDate;
        this.activityTypes = new ArrayList<>();
        this.organisers = new HashSet<>();
        this.participants = new HashSet<>();
        this.followers = new HashSet<>();
        this.sharedUsers = new HashSet<>();
        this.hashtags = new HashSet<>();
        this.outcomeQuestions = new HashSet<>();
        this.creationDate = creationDate;
        this.visibility = visibility;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public List<ActivityType> getActivityTypes() {
        return activityTypes;
    }

    public void setActivityTypes(List<ActivityType> activityTypes) {
        this.activityTypes = activityTypes;
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

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public Set<User> getOrganisers() {
        return organisers;
    }

    public void setOrganisers(Set<User> organisers) {
        this.organisers = organisers;
    }

    public Set<User> getParticipants() {
        return participants;
    }

    public void setParticipants(Set<User> participants) {
        this.participants = participants;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Set<User> getFollowers() {
        return followers;
    }

    public void setFollowers(Set<User> followers) {
        this.followers = followers;
    }

    public Set<User> getSharedUsers() {
        return sharedUsers;
    }

    public void addSharedUser(User user) {
        this.sharedUsers.add(user);
    }

    public void clearSharedUsers() {
        this.sharedUsers = new HashSet<>();
    }

    public Set<Hashtag> getHashtags() {
        return hashtags;
    }

    public void setHashtags(Set<Hashtag> hashtags) {
        this.hashtags = hashtags;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public List<UserActivityHistory> getHistories() {
        return histories;
    }

    public void setHistories(List<UserActivityHistory> histories) {
        this.histories = histories;
    }

    /**
     * Checks if the given user has sufficient permissions to CRUD the activity
     *
     * @param user to be checked
     * @return true if user has sufficient privilege
     */
    public boolean userHasEditPrivilege(User user) {
        return creator.getId().equals(user.getId()) || user.getIsAdmin() || organisers.contains(user);
        // Can later use this to check if user has admin on this activity
    }

    /**
     * Checks if the given user has sufficient permissions to CRUD the activity
     *
     * @param user to be checked
     * @return true if user has sufficient privilege
     */
    public boolean userHasDeletePrivilege(User user) {
        return creator == user || user.getIsAdmin();
        // Can later use this to check if user has admin on this activity
    }

    public ActivityVisibility getVisibility() {
        return visibility;
    }

    public void setVisibility(ActivityVisibility visibility) {
        this.visibility = visibility;
    }

    public Set<OutcomeQuestion> getOutcomeQuestions() {
            return outcomeQuestions;
        }

    public void setOutcomeQuestions(Set<OutcomeQuestion> outcomeQuestionSet) {
        this.outcomeQuestions = outcomeQuestionSet;
    }

    public List<Photo> getImages() {
        return images;
    }

    public void setImages(List<Photo> images) {
        this.images = images;
    }

    @Override
    public String toString() {
        return "Activity{" +
                "id=" + id +
                ", creator=" + creator +
                ", participants=" + participants +
                ", activityName='" + activityName + '\'' +
                ", description='" + description + '\'' +
                ", activityTypes=" + activityTypes +
                ", isContinuous=" + isContinuous +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", creationDate=" + creationDate +
                ", location='" + location + '\'' +
                '}';
    }
}

package com.springvuegradle.team200.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.*;

/**
 * A class to represent a user, and store any associated details.
 */
@Entity
@Table(name = "app_user")
public class User implements UserDetails {

    /**
     * The user's unique ID
     */
    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private Long id;

    @Version
    private Long version;

    /**
     * Whether the user has admin privileges
     */
    @Column(name = "is_admin")
    private Boolean isAdmin = false;

    /**
     * Whether the user is the auto-generated global admin
     */
    @Column(name = "is_global_admin")
    private Boolean isGlobalAdmin = false;

    /**
     * The user's hashed and salted password
     */
    @Column
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    /**
     * User's first name, required
     */
    @Column
    private String firstName;

    /**
     * User's middle name, optional
     */
    @Column
    private String middleName;

    /**
     * User's last name, required
     */
    @Column
    private String lastName;

    /**
     * The user's gender
     */
    @Column
    private String gender;

    /**
     * A String description of the user, as written by them.
     */
    @Column
    private String bio;

    /**
     * The user's fitness level
     */
    @Column
    private int fitnessLevel;

    /**
     * The user's associated username
     */
    @Column
    private String username;

    /**
     * The user's date of birth
     */
    @Column
    private String birthDate;

    /**
     * The user's {@link Location}
     */
    @Embedded
    @AttributeOverrides( {
            @AttributeOverride(name="locationPlaceId", column = @Column(name="location_place_id") ),
            @AttributeOverride(name="description", column = @Column(name="location_description") ),
            @AttributeOverride(name="latitude", column = @Column(name="location_latitude") ),
            @AttributeOverride(name="longitude", column = @Column(name="location_longitude") ),
    } )
    private Location location;

    /**
     * A List of {@link UserEmail} objects associated with the user's account
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserEmail> userEmails = new ArrayList<>();

    /**
     * A list of {@link PassportCountry} objects associated with the user
     */
    @ManyToMany
    @JoinTable(
            name = "user_passport_country",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "country_id"))
    private List<PassportCountry> passports = new ArrayList<>();


    /**
     * A list of {@link ActivityType} objects associated with the user
     */
    @ManyToMany
    @JoinTable(
            name = "user_activity_types",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "activity_type_id"))
    private List<ActivityType> activityTypes = new ArrayList<>();


    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "user")
    List<OutcomeUserAnswer> outcomeAnswers;

    /**
     * A list of {@link Activity} objects that the user is following
     */
    @ManyToMany
    @JoinTable(
            name = "user_activity_followers",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "activity_id"))
    private Set<Activity> activitiesFollowing = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "user_activity_participation",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "activity_id")
    )
    private Set<Activity> activityParticipator = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "user_activity_organisers",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "activity_id")
    )
    private Set<Activity> activityOrganiser = new HashSet<>();

    @OneToMany(cascade = {CascadeType.ALL}, mappedBy = "creator", fetch = FetchType.LAZY)
    private Set<Activity> activityCreator = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<UserActivityHistory> histories;

    @OneToMany(mappedBy="user", cascade = CascadeType.REMOVE)
    private List<Photo> images = new ArrayList<>();

    /**
     * Empty constructor
     */
    public User() {
    }

    /**
     * Constructor intended for dev use only
     *
     * @param username Username for the newly constructed user
     * @param password Password for the newly constructed user
     */
    public User(String username, String password) {
        this(username, password, "Firstname", "Middlename", "Lastname", "Long description", "Male", username,
                "2020-01-01", 3);
    }

    /**
     * Constructor
     *
     * @param primaryEmail The user's primary email
     * @param password     The user's password
     * @param fname        The user's first name
     * @param mname        The user's middle name
     * @param lname        The user's last name
     * @param bio          The user's bio
     * @param gender       The user's gender as a String
     * @param username     The username associated with the user
     * @param birthDate    The user's birthdate as a String
     * @param fitness      The user's fitness level as an integer
     */
    public User(String primaryEmail, String password, String fname, String mname, String lname, String bio,
                String gender, String username, String birthDate, int fitness) {
        this.password = password;
        this.firstName = fname;
        this.middleName = mname;
        this.lastName = lname;
        this.bio = bio;
        this.gender = gender;
        this.username = username;
        this.birthDate = birthDate;
        this.fitnessLevel = fitness;
        userEmails.add(new UserEmail(primaryEmail, this, true));
        this.outcomeAnswers = new ArrayList<>();
        this.activityParticipator = new HashSet<>();
        this.activityCreator = new HashSet<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public boolean getIsGlobalAdmin() {
        return isGlobalAdmin;
    }

    public void setIsGlobalAdmin(Boolean isGlobalAdmin) {
        this.isAdmin = true;
        this.isGlobalAdmin = isGlobalAdmin;
    }

    public List<PassportCountry> getPassports() {
        return passports;
    }

    public void setPassports(List<PassportCountry> passports) {
        this.passports = passports;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public int getFitnessLevel() {
        return fitnessLevel;
    }

    public void setFitnessLevel(int fitnessLevel) {
        this.fitnessLevel = fitnessLevel;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public List<UserEmail> getUserEmails() {
        return userEmails;
    }

    public void setUserEmails(List<UserEmail> userEmails) {
        this.userEmails = userEmails;
    }

    public List<ActivityType> getActivityTypes() {
        return activityTypes;
    }

    public void setActivityTypes(List<ActivityType> activityTypes) {
        this.activityTypes = activityTypes;
    }

    public Long getVersion() {
        return version;
    }

    public void removeActivity(Activity activity) {
        List<Set<Activity>> relations = List.of(activitiesFollowing, activityCreator,
                activityOrganiser, activityParticipator);
        for (Set<Activity> relation : relations) {
            relation.remove(activity);
        }
    }

    public List<UserActivityHistory> getHistories() {
        return histories;
    }

    public void setHistories(List<UserActivityHistory> histories) {
        this.histories = histories;
    }

    /**
     * Returns the user's primary email
     * Assumes that there is only one primary email
     *
     * @return primary email of user
     */
    public UserEmail getPrimaryEmail() {
        for (var userEmail : getUserEmails()) {
            if (userEmail.getIsPrimary()) {
                return userEmail;
            }
        }
        return null;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    /**
     * Deletes the user's role in an activity for all roles
     *
     * @param activity The activity to remove the user's role for
     */
    public void deleteRole(Activity activity) {
        removeActivityCreation(activity);
        removeActivityOrganising(activity);
        removeActivityParticipation(activity);
        removeActivityFollowing(activity);
    }

    public Set<Activity> getActivityCreator() {
        return activityCreator;
    }

    public void setActivityCreator(Set<Activity> activityCreator) {
        this.activityCreator = activityCreator;
    }

    /**
     * Add an activity to the set of activities created by the user
     *
     * @param activity The activity the user created
     */
    public void addActivityCreator(Activity activity) {
        deleteRole(activity);
        this.activityCreator.add(activity);
    }

    /**
     * Remove the given activity from the list of activities created by the user
     *
     * @param activity The activity to no longer have created
     */
    public void removeActivityCreation(Activity activity) {
        this.activityCreator.remove(activity);
    }

    public Set<Activity> getActivityOrganiser() {
        return this.activityOrganiser;
    }

    public void setActivityOrganiser(Set<Activity> activities) {
        this.activityOrganiser = activities;
    }

    /**
     * Add an activity to the set of activities organised by the user
     *
     * @param activity The activity the user is organising
     */
    public void addActivityOrganising(Activity activity) {
        deleteRole(activity);
        this.activityOrganiser.add(activity);
    }

    /**
     * Remove the given activity from the set of activities organised by the user
     *
     * @param activity The activity to no longer organise
     */
    public void removeActivityOrganising(Activity activity) {
        this.activityOrganiser.remove(activity);
    }

    public Set<Activity> getActivityParticipator() {
        return activityParticipator;
    }

    public void setActivityParticipator(Set<Activity> activityParticipator) {
        this.activityParticipator = activityParticipator;
    }

    /**
     * Add an activity to the set of activities participated in by the user,
     * if the activity is not already added
     *
     * @param activity The activity to participate in
     */
    public void addActivityParticipation(Activity activity) {
        deleteRole(activity);
        this.activityParticipator.add(activity);
    }

    /**
     * Remove the given activity from the set of activities followed by the user
     *
     * @param activity The activity to no longer participate in
     */
    public void removeActivityParticipation(Activity activity) {
        this.activityParticipator.remove(activity);
    }

    public Set<Activity> getActivitiesFollowing() {
        return activitiesFollowing;
    }

    public void setActivitiesFollowing(Set<Activity> activitiesFollowing) {
        this.activitiesFollowing = activitiesFollowing;
    }

    /**
     * Add an activity to the list of activities followed by the user,
     * if the activity is not already added
     *
     * @param activity The activity to follow
     */
    public void addActivityFollowing(Activity activity) {
        deleteRole(activity);
        this.activitiesFollowing.add(activity);
    }

    /**
     * Remove the given activity from the list of activities followed by the user
     *
     * @param activity The activity to unfollow
     */
    public void removeActivityFollowing(Activity activity) {
        this.activitiesFollowing.remove(activity);
    }

    /**
     * Part of the UserDetails class
     *
     * @return collection of all granted authorities
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new ArrayList<>();
    }

    /**
     * Part of UserDetails
     *
     * @return whether the account is non expired
     */
    @Override
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Part of UserDetails
     *
     * @return whether the account is non locked
     */
    @Override
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Part of UserDetails
     *
     * @return whether the account's credentials have not expired
     */
    @Override
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Part of UserDetails
     *
     * @return whether the account is enabled
     */
    @Override
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", password='" + password + '\'' +
                ", firstName='" + firstName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", userName='" + username + '\'' +
                ", gender='" + gender + '\'' +
                ", bio='" + bio + '\'' +
                ", fitnessLevel=" + fitnessLevel +
                ", username='" + username + '\'' +
                ", birthDate='" + birthDate + '\'' +
                ", passports=NOT WORKING" +
                ", userEmails=NOT WORKING" +
                '}';
    }

    /**
     * Method called before deleting the user entity
     * Removes ownership from all activities
     */
    @PreRemove
    private void preRemove() {
        for (var activity : getActivityCreator()) {
            activity.setCreator(null);
        }
    }

    /**
     * Overrides the default equals.
     *
     * @param obj The Object to compare to, which must be cast to a User
     * @return A boolean, true if they are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) return true;
        if (!(obj instanceof User)) return false;
        User other = (User) obj;
        return Objects.equals(other.getId(), getId()) //
                && Objects.equals(other.getIsAdmin(), getIsAdmin())
                && Objects.equals(other.getIsGlobalAdmin(), getIsGlobalAdmin())
                && Objects.equals(other.getFirstName(), getFirstName()) //
                && Objects.equals(other.getMiddleName(), getMiddleName()) //
                && Objects.equals(other.getLastName(), getLastName()) //
                && Objects.equals(other.getUsername(), getUsername())
                && Objects.equals(other.getPassword(), getPassword())
                && Objects.equals(other.getBio(), getBio()) //
                && Objects.equals(other.getBirthDate(), getBirthDate())
                && Objects.equals(other.getGender(), getGender())
                && Objects.equals(other.getFitnessLevel(), getFitnessLevel());
    }

    @Override
    public int hashCode() {
        int i = 0;
        i = 31 * i + (getId() == null ? 0 : getId().hashCode());
        i = 31 * i + (getFirstName() == null ? 0 : getFirstName().hashCode());
        i = 31 * i + (getMiddleName() == null ? 0 : getMiddleName().hashCode());
        i = 31 * i + (getLastName() == null ? 0 : getLastName().hashCode());
        i = 31 * i + (getUsername() == null ? 0 : getUsername().hashCode());
        i = 31 * i + (getPassword() == null ? 0 : getPassword().hashCode());
        i = 31 * i + (getBio() == null ? 0 : getBio().hashCode());
        i = 31 * i + (getGender() == null ? 0 : getGender().hashCode());
        return i;
    }

    public List<Photo> getImages() {
        return images;
    }

    public void setImages(List<Photo> images) {
        this.images = images;
    }
}

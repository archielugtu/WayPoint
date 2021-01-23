package com.springvuegradle.team200.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.springvuegradle.team200.model.*;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Limited response of user data that is publicly visible to any viewer
 */
@ResponseBody
public class UserResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("firstname")
    private String firstName;

    @JsonProperty("middlename")
    private String middleName;

    @JsonProperty("lastname")
    private String lastName;

    @JsonProperty("nickname")
    private String username;

    @JsonProperty("bio")
    private String bio;

    @JsonProperty("gender")
    private String gender;

    @JsonProperty("primary_email")
    private UserEmail primaryEmail;

    @JsonProperty("additional_email")
    private List<UserEmail> additionalEmail;

    @JsonProperty("date_of_birth")
    private String birthDate;

    @JsonProperty("passports")
    private List<PassportCountry> passports;

    @JsonProperty("activities")
    private List<ActivityType> activities;

    @JsonProperty("fitness")
    private int fitness;

    @JsonProperty("location")
    private Location location;

    /**
     * Constructor
     *
     * @param u The User being retrieved
     * @throws IllegalStateException Thrown if there is no primary email address associated with the user
     */
    public UserResponse(User u) {
        this.id = u.getId();
        this.firstName = u.getFirstName();
        this.middleName = u.getMiddleName();
        this.lastName = u.getLastName();
        this.username = u.getUsername();
        this.bio = u.getBio();
        this.gender = u.getGender();
        this.birthDate = u.getBirthDate();
        this.primaryEmail = u.getUserEmails()
                .stream()
                .filter(UserEmail::getIsPrimary)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Critically missing data: primary email address."));
        this.additionalEmail = u.getUserEmails()
                .stream()
                .filter(ue -> !ue.getIsPrimary())
                .collect(Collectors.toList());
        this.fitness = u.getFitnessLevel();
        this.passports = u.getPassports();
        this.location = u.getLocation();
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getGender() {
        return gender;
    }

    public String getBio() {
        return bio;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public UserEmail getPrimaryEmail() {
        return primaryEmail;
    }

    public List<UserEmail> getAdditionalEmail() {
        return additionalEmail;
    }

    public List<PassportCountry> getPassports() {
        return passports;
    }

    public int getFitness() {
        return fitness;
    }

    public String getUsername() {
        return username;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}

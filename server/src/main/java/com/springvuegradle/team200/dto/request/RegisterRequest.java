package com.springvuegradle.team200.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.springvuegradle.team200.model.Location;
import com.springvuegradle.team200.validator.Between;
import com.springvuegradle.team200.validator.EmailList;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;
import java.util.Date;
import java.util.List;

public class RegisterRequest {
    @NotBlank(message = "First name cannot be blank")
    @Size(max = 50, message = "First name cannot be over 50 characters")
    @JsonProperty("firstname")
    private String firstName;

    @Size(max = 50, message = "Middle name cannot be over 50 characters")
    @JsonProperty("middlename")
    private String middleName;

    @Size(max = 50, message = "Last name cannot be over 50 characters")
    @NotBlank(message = "Last name cannot be blank")
    @JsonProperty("lastname")
    private String lastName;

    @Size(max = 50, message = "Nickname cannot be over 50 characters")
    @JsonProperty("nickname")
    private String username;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, message = "Your password must contain at least 8 characters")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d).+$",
            message = "Your password must have a mix of upper and lower case letters, " +
                    "and contain at least 1 number")
    @JsonProperty("password")
    private String password;

    @Size(max = 200, message = "Bio cannot be over 200 characters")
    @JsonProperty("bio")
    private String bio;

    @Pattern(regexp = "male|female|non-binary",
            flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "Gender must be male, female or non-binary")
    @NotBlank(message = "Gender cannot be blank")
    @JsonProperty("gender")
    private String gender;

    @Email(message = "Email is not in valid email format")
    @NotBlank(message = "Email address cannot be blank")
    @JsonProperty("primary_email")
    private String emailAddress;

    @Min(value = 0)
    @Max(value = 4)
    @JsonProperty("fitness")
    private Integer fitnessLevel;

    @Between(upperBound = -9, lowerBound = -100, message = "Age requirement is not met")
    @DateTimeFormat(pattern = "yyyy-mm-dd")
    @JsonProperty("date_of_birth")
    private Date birthDate;

    @JsonProperty("passports")
    private List<String> passports;

    @JsonProperty("activities")
    private List<String> activities;

    @EmailList
    @Size(max = 4, message = "Cannot have more than 4 additional emails")
    @JsonProperty("additional_email")
    private List<String> additionalEmails;

    @JsonProperty("location")
    private Location location;


    /**
     * This constructor is not used in the controller but useful for test
     */
    public RegisterRequest() {
        //empty constructor
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBio() {
        return bio;
    }

    // Setters are useful for testing purposes

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public Integer getFitnessLevel() {
        return fitnessLevel;
    }

    public void setFitnessLevel(Integer fitnessLevel) {
        this.fitnessLevel = fitnessLevel;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public List<String> getPassports() {
        return passports;
    }

    public void setPassports(List<String> passports) {
        this.passports = passports;
    }

    public List<String> getActivities() {
        return activities;
    }

    public void setActivities(List<String> activities) {
        this.activities = activities;
    }

    public List<String> getAdditionalEmails() {
        return additionalEmails;
    }

    public void setAdditionalEmails(List<String> additionalEmails) {
        this.additionalEmails = additionalEmails;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}

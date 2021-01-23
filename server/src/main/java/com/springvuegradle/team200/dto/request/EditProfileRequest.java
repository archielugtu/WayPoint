package com.springvuegradle.team200.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.springvuegradle.team200.model.Location;
import com.springvuegradle.team200.validator.Between;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EditProfileRequest {

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
    private String nickname;

    @Size(max = 200, message = "Bio cannot be over 200 characters")
    @JsonProperty("bio")
    private String bio;

    @Pattern(regexp = "male|female|non-binary",
            flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "Gender must be male, female or non-binary")
    @NotBlank(message = "Gender cannot be blank")
    @JsonProperty("gender")
    private String gender;

    @Min(value = 0)
    @Max(value = 4)
    @JsonProperty("fitness")
    private int fitnessLevel;

    @Email(message = "Email is not in valid email format")
    @NotBlank(message = "Email address cannot be blank")
    @JsonProperty("primary_email")
    private String primaryEmail;

    @Size(max = 4, message = "Cannot have more than 4 additional emails")
    @JsonProperty("additional_email")
    private List<String> additionalEmails = new ArrayList<>();

    @Between(upperBound = -10, lowerBound = -100, message = "Age requirement is not met")
    @DateTimeFormat(pattern = "yyyy-mm-dd")
    @JsonProperty("date_of_birth")
    private Date birthDate;

    @JsonProperty("passports")
    private List<String> passports = new ArrayList<>();

    @JsonProperty("location")
    private Location location;

    @JsonProperty("version")
    private Long version;

    public EditProfileRequest() {
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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getFitnessLevel() {
        return fitnessLevel;
    }

    public void setFitnessLevel(int fitnessLevel) {
        this.fitnessLevel = fitnessLevel;
    }

    public String getPrimaryEmail() {
        return primaryEmail;
    }

    public void setPrimaryEmail(String primaryEmail) {
        this.primaryEmail = primaryEmail;
    }

    public List<String> getAdditionalEmails() {
        return additionalEmails;
    }

    public void setAdditionalEmails(List<String> additionalEmails) {
        this.additionalEmails = additionalEmails;
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

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}


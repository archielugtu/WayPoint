package com.springvuegradle.team200.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.springvuegradle.team200.model.ActivityType;
import com.springvuegradle.team200.model.User;
import com.springvuegradle.team200.model.UserEmail;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SearchUserResponse {

    @JsonProperty("lastname")
    private String lastName;

    @JsonProperty("firstname")
    private String firstName;

    @JsonProperty("middlename")
    private String middleName;

    @JsonProperty("nickname")
    private String nickname;

    @JsonProperty("primary_email")
    private String primaryEmail;

    @JsonProperty("activities")
    private List<String> activities;

    public static SearchUserResponse of(User user) {
        SearchUserResponse searchUserResponse = new SearchUserResponse();
        searchUserResponse.setFirstName(user.getFirstName());
        searchUserResponse.setLastName(user.getLastName());
        searchUserResponse.setMiddleName(user.getMiddleName());
        searchUserResponse.setNickname(user.getUsername());
        Optional<UserEmail> userPrimaryEmail = user.getUserEmails()
                .stream()
                .filter(UserEmail::getIsPrimary)
                .findFirst();
        userPrimaryEmail.ifPresent(userEmail ->
                searchUserResponse.setPrimaryEmail(userEmail.getAddress())
        );
        searchUserResponse.setActivities(user.getActivityTypes()
                .stream()
                .map(ActivityType::getType)
                .sorted()
                .collect(Collectors.toList())
        );
        return searchUserResponse;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPrimaryEmail() {
        return primaryEmail;
    }

    public void setPrimaryEmail(String primaryEmail) {
        this.primaryEmail = primaryEmail;
    }

    public List<String> getActivities() {
        return activities;
    }

    public void setActivities(List<String> activities) {
        this.activities = activities;
    }
}

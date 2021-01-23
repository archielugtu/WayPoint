package com.springvuegradle.team200.steps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.jsonpath.JsonPath;
import com.springvuegradle.team200.AbstractCucumber;
import com.springvuegradle.team200.model.Activity;
import com.springvuegradle.team200.model.ActivityType;
import com.springvuegradle.team200.model.User;
import com.springvuegradle.team200.model.UserEmail;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.PendingException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

import javax.servlet.http.Cookie;
import javax.transaction.Transactional;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

public class UserSteps extends AbstractCucumber {

    public UserSteps() throws ParseException {
        super();
    }

    @Transactional
    void setupSingleUser(User user, String name, String lName, List<String> activityTypes) {
        List<ActivityType> activityTypeList = new ArrayList<>();
        for (String s : activityTypes) {
            activityTypeList.add(activityTypesRepository.findByType(s).get());
        }
        user.setActivityTypes(activityTypeList);
        user.setUsername(name);
        user.setFirstName(name);
        user.setLastName(lName);
        user.setUserEmails(List.of(new UserEmail(name + "@" + "test.com", user, true)));
        userRepository.save(user);
    }

    @Given("I have a user named {string}")
    @Transactional
    public void iHaveAUserNamed(String name) {
        User user = new User();
        setupSingleUser(user, name, "Cucumber", List.of("Running"));
    }

    @Then("the user named {string} will have {int} update on their feed")
    public void theUserNamedWillHaveUpdateOnTheirFeed(String firstName, int numUpdate) throws Exception {
        User user = userRepository.getUserFromFirstName(firstName);
        latestMockMvcResult = mockMvc.perform(get("/profiles/" + user.getId() + "/subscriptions/activities/history")
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(user))))
                .andReturn();
        String response = latestMockMvcResult.getResponse().getContentAsString();
        Assertions.assertEquals(numUpdate, JsonPath.parse(response).read("$.total_elements", Integer.class));
    }

    @When("a PUT request edits the user named {string} to have locationPlaceId {string}")
    public void aPUTRequestEditsTheUserNamedToHaveLocation(String username, String locationPlaceId) throws Exception {
        editProfileRequest.setFirstName(username);
        editProfileRequest.setLastName(username);
        editProfileRequest.setBirthDate(birthDate);
        editProfileRequest.setPrimaryEmail("T@T.com");
        editProfileRequest.setGender("Male");
        User user = userRepository.getUserFromFirstName(username);
        newLocation.setPlaceId(locationPlaceId);
        editProfileRequest.setLocation(newLocation);
        latestMockMvcResult = mockMvc.perform(put("/profiles/" + user.getId())
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(user)))
                .header("If-Match", user.getVersion())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(editProfileRequest)))
                .andReturn();
    }

    @Then("the response code {int} is received for the user")
    public void responseCodeReceived(int code) {
        assertEquals(code, latestMockMvcResult.getResponse().getStatus());
    }

    @And("the user named {string} will have the locationPlaceId {string}")
    public void theUserNamedWillHaveTheLocation(String username, String locationPlaceId) {
        User user = userRepository.getUserFromFirstName(username);
        assertEquals(locationPlaceId, user.getLocation().getPlaceId());
    }
}
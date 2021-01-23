package com.springvuegradle.team200.steps;

import com.jayway.jsonpath.JsonPath;
import com.springvuegradle.team200.AbstractCucumber;
import com.springvuegradle.team200.dto.request.SearchActivityRequest;
import com.springvuegradle.team200.model.*;
import com.springvuegradle.team200.repository.UserRepository;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.PendingException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.test.web.servlet.MvcResult;

import javax.servlet.http.Cookie;
import javax.transaction.Transactional;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.assertTrue;
import static junit.framework.TestCase.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ActivitySteps extends AbstractCucumber {

    public ActivitySteps() throws ParseException {
        super();
    }

    @Given("I have an activity named {string}")
    @Transactional
    public void iHaveAnActivityNamed(String name) throws Exception {
        globalAdmin = userRepository.findByIsGlobalAdminTrue();

        // Write code here that turns the phrase above into concrete actions
        final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        Date startDate = formatter.parse("2020-11-20T08:00:00+1300");
        Date endDate = formatter.parse("2020-12-20T08:00:00+1300");
        Date creationDate = formatter.parse("2020-01-01T08:00:00+1300");
        Activity marathon = new Activity(name, "Cucumber", new Location(), false, startDate, endDate, creationDate,
                ActivityVisibility.PUBLIC);



        ActivityType running = activityTypesRepository.getActivityTypesFromName("Running");
        List<ActivityType> activityActivityTypes = List.of(running);
        Set<Hashtag> startingHashtags = Set.of(new Hashtag("#yes"), new Hashtag("#no"));
        hashtagRepository.saveAll(startingHashtags);

        marathon.setHashtags(startingHashtags);
        marathon.setActivityTypes(activityActivityTypes);
        marathon.setCreator(userRepository.findByIsGlobalAdminTrue());
        activityRepository.save(marathon);

        Activity activity = new Activity(name, "Cucumber", new Location(), false, startDate, endDate, creationDate,
                ActivityVisibility.PUBLIC);
        activity.setCreator(globalAdmin);
        activityRepository.save(activity);
    }

    @Transactional
    @Given("I have an activity named {string} created by the user named {string}")
    public void iHaveAnActivityNamed(String name, String firstName) throws Exception {
        User user = userRepository.getUserFromFirstName(firstName);
        activityRequest.setActivityName(name);
        Activity activity = userActivityService.create(user.getId(), activityRequest);
        activityRepository.save(activity);
    }

    @Given("the user named {string} has responded to the question {string} with {string}")
    public void theUserNamedHasRespondedToTheQuestionWith(String userName, String question, String response) {
        throw new io.cucumber.java.PendingException();
    }

    @Given("the user named {string} has the role {string} for the activity named {string}")
    @Transactional
    public void TheUserHasTheRoleForActivity(String firstName, String roleName, String activityName) throws Exception {
        User user = userRepository.getUserFromFirstName(firstName);
        Activity activity = activityRepository.findFirstByActivityName(activityName);
        switch (roleName.toLowerCase()) {
            case "participant":
                var participator = user.getActivityParticipator();
                participator.add(activity);
                user.setActivityParticipator(participator);
                userRepository.save(user);
                break;
            case "creator":
                var creator = user.getActivityCreator();
                creator.add(activity);
                user.setActivityCreator(creator);
                userRepository.save(user);
                break;
            case "organiser":
            case "follower":
                var follower = user.getActivitiesFollowing();
                follower.add(activity);
                user.setActivitiesFollowing(follower);
                userRepository.save(user);
                break;
            default:
                throw new Exception("role name not found");
        }
        activityRepository.save(activity);
    }

    @Given("the activity named {string} has the outcomes")
    public void theActivityHasTheOutcomes(String activityName, DataTable outcomes) {
        Activity activity = activityRepository.findFirstByActivityName(activityName);
        Set<OutcomeQuestion> outcomeQuestions = getOutcomeQuestions(outcomes);
        activity.setOutcomeQuestions(outcomeQuestions);
    }

    /**
     * Converts  outcomes datatable into set of OutcomeQuestions
     *
     * @param outcomes datatable of outcomes
     * @return Set of OutcomeQuesiton objects
     */
    private Set<OutcomeQuestion> getOutcomeQuestions(DataTable outcomes) {
        Map<Object, Object> data = outcomes.asMap(String.class, String.class);
        Set<OutcomeQuestion> outcomeQuestions = new HashSet<>();
        for (var entry : data.entrySet()) {
            String question = (String) entry.getKey();
            String inputType = (String) entry.getValue();
            OutcomeInputType outcomeInputType = OutcomeInputType.valueOf(inputType.toUpperCase());
            OutcomeQuestion outcomeQuestion = new OutcomeQuestion(question, outcomeInputType, null);
            outcomeQuestions.add(outcomeQuestion);
        }
        return outcomeQuestions;
    }

    @When("a POST request adds an activity named {string} with the outcomes")
    public void PostActivityWithOutcomes(String activityName, DataTable outcomes) throws Exception {

        // ActivityEditRequest has some default data that might break tests. make sure you set the activityname
        activityRequest.setOutcomeQuestions(getOutcomeQuestions(outcomes));
        activityRequest.setActivityName(activityName);

        // Reload the globalAdmin because cucumber loses track of it somehow
        globalAdmin = userRepository.findByIsGlobalAdminTrue();

        mockMvc.perform(post("/profiles/" + globalAdmin.getId() + "/activities/")
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(globalAdmin)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(activityRequest)));
    }

    @When("a PUT request edits the activity named {string} to have the outcomes")
    @Transactional
    public void PutActivityWithOutcomes(String activityName, DataTable outcomes) throws Exception {
        Activity activity = activityRepository.findFirstByActivityName(activityName);

        // ActivityEditRequest has some default data that might break tests. make sure you set the activityname
        activityEditRequest.setOutcomeQuestions(getOutcomeQuestions(outcomes));
        activityEditRequest.setActivityName(activityName);

        // Reload the globalAdmin because cucumber loses track of it somehow
        globalAdmin = userRepository.findByIsGlobalAdminTrue();

        latestMockMvcResult = mockMvc.perform(put("/profiles/" + globalAdmin.getId() + "/activities/" + activity.getId())
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(globalAdmin)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(activityEditRequest)))
                .andReturn();

    }

    @When("a PUT request edits the activity named {string} to have no outcomes")
    public void PutActivityWithoutOutcomes(String activityName) throws Exception {
        Activity activity = activityRepository.findFirstByActivityName(activityName);

        // ActivityEditRequest has some default data that might break tests. make sure you set the activityname
        activityEditRequest.setOutcomeQuestions(Set.of());
        activityEditRequest.setActivityName(activityName);

        // Reload the globalAdmin because cucumber loses track of it somehow
        globalAdmin = userRepository.findByIsGlobalAdminTrue();

        latestMockMvcResult = mockMvc.perform(put("/profiles/" + globalAdmin.getId() + "/activities/" + activity.getId())
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(globalAdmin)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(activityEditRequest)))
                .andReturn();
    }

    @Then("the activity named {string} has {int} outcome options")
    @Transactional
    public void ActivityOutcomesCount(String activityName, int outcomeCount) {
        Activity activity = activityRepository.findFirstByActivityName(activityName);
        assertEquals(outcomeCount, activity.getOutcomeQuestions().size());
    }

    @Then("the response code {int} is received")
    public void responseCodeReceived(int code) {
        assertEquals(code, latestMockMvcResult.getResponse().getStatus());
    }


    @When("a follow POST request for the activity {string} is sent from the user named {string}")
    @Transactional
    public void aFollowPOSTRequestForTheActivityIsSentFromTheUserNamed(String activityName,
                                                                       String firstName) throws Exception {
        User user = userRepository.getUserFromFirstName(firstName);
        Optional<Activity> activity = activityRepository.findByActivityName(activityName);

        mockMvc.perform(post("/profiles/" + user.getId() + "/subscriptions/activities/" + activity.get().getId())
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(user)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(editActivityRoleRequest)))
                .andReturn();
    }

    @Then("the activity named {string} has {int} follower\\(s)")
    @Transactional
    public void theActivityNamedHasFollowerS(String activityName, int numFollowers) {
        Optional<Activity> activity = activityRepository.findByActivityName(activityName);
        assertEquals(numFollowers, activity.get().getFollowers().size());
    }

    @Given("the activity named {string} has the user named {string} as a follower")
    @Transactional
    public void theActivityNamedHasTheUserNamedAsAFollower(String activityName, String firstName) throws Exception {
        User user = userRepository.getUserFromFirstName(firstName);
        Optional<Activity> activity = activityRepository.findByActivityName(activityName);
        Set<User> followers = new HashSet<>();
        followers.add(user);
        activity.get().setFollowers(followers);
        activityRepository.save(activity.get());
        assertTrue(activityRepository.findByActivityName(activityName).get().getFollowers().contains(user));
    }

    @When("an unfollow DELETE request for the activity {string} is sent from the user named {string}")
    @Transactional
    public void anUnfollowDELETERequestForTheActivityIsSentFromTheUserNamed(String activityName,
                                                                            String firstName) throws Exception {
        User user = userRepository.getUserFromFirstName(firstName);
        Optional<Activity> activity = activityRepository.findByActivityName(activityName);
        mockMvc.perform(delete("/profiles/" + user.getId() + "/subscriptions/activities/" + activity.get().getId())
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(user))))
                .andReturn();
    }

    @When("a PUT request edits the activity named {string} to have the name {string} form the user named {string}")
    public void aPUTRequestEditsTheActivityNamedToHaveTheNameFormTheUserNamed(String activityName,
                                                                              String newActivityName,
                                                                              String firstName) throws Exception {
        User user = userRepository.getUserFromFirstName(firstName);
        Optional<Activity> activity = activityRepository.findByActivityName(activityName);
        activityEditRequest.setActivityName(newActivityName);
        mockMvc.perform(put("/profiles/" + user.getId() + "/activities/" + activity.get().getId())
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(user)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(activityEditRequest)))
                .andReturn();
    }

    @And("the activity named {string} is received")
    public void theActivityNamedIsReceived(String activityName) throws UnsupportedEncodingException {
        String response = latestMockMvcResult.getResponse().getContentAsString();
        assertTrue(activityName.equals(JsonPath.parse(response).read("$.results[0].activity_name")) ||
                        activityName.equals(JsonPath.parse(response).read("$.results[1].activity_name"))
                );
    }

    @When("a GET request is sent by a user named {string} to retrieve an activity with the keyword {string}")
    public void aGETRequestIsSentByAUserNamedToRetrieveAnActivityWithTheKeyword(String firstName, String keyWord) throws Exception {
        User user = userRepository.getUserFromFirstName(firstName);
        searchActivityRequest = new SearchActivityRequest();
        searchActivityRequest.setP(0);
        searchActivityRequest.setSize(20);
        searchActivityRequest.setName(keyWord);
        latestMockMvcResult = mockMvc.perform(get("/activities")
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(user)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(searchActivityRequest)))
                .andReturn();
    }

    @And("the activity named {string} is not received")
    public void theActivityNamedIsNotReceived(String activityName) throws UnsupportedEncodingException {
        String response = latestMockMvcResult.getResponse().getContentAsString();
        assertNotEquals(activityName, JsonPath.parse(response).read("$.results[0].activity_name"));
    }

    @Given("the activity named {string} has the coordinates {float}, {float}")
    public void theActivityNamedHasTheCoordinates(String activityName, Float lat, Float lng) {
        Activity activity = activityRepository.findFirstByActivityName(activityName);
        Location location = new Location();
        location.setLongitude(lng);
        location.setLatitude(lat);
        activity.setLocation(location);
        activityRepository.save(activity);
    }

    @When("a GET request is sent to retrieve activities within the boundaries {float}, {float}, {float}, {float}")
    public void aGETRequestIsSentByAUserNamedToRetrieveActivitiesWithinTheBoundaries(Float sw_lat, Float sw_lng, Float ne_lat, Float ne_lng) throws Exception{
        globalAdmin = userRepository.findByIsGlobalAdminTrue();
        latestMockMvcResult = mockMvc.perform(get("/activities")
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(globalAdmin)))
                .param("sw_lat", String.valueOf(sw_lat))
                .param("sw_lng", String.valueOf(sw_lng))
                .param("ne_lat", String.valueOf(ne_lat))
                .param("ne_lng", String.valueOf(ne_lng)))
                .andReturn();

    }
}

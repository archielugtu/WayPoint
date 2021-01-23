package com.springvuegradle.team200.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.springvuegradle.team200.dto.request.ActivityRequest;
import com.springvuegradle.team200.jwt.JwtTokenUtil;
import com.springvuegradle.team200.model.*;
import com.springvuegradle.team200.repository.*;
import com.springvuegradle.team200.service.UserActivityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.servlet.http.Cookie;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ActivityTest {

    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    Date startDate = formatter.parse("2020-11-20T08:00:00+1300");
    Date endDate = formatter.parse("2020-12-20T08:00:00+1300");
    Date creationDate = formatter.parse("2020-01-01T08:00:00+1300");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private HashtagRepository hashtagRepository;

    @Autowired
    private ActivityTypesRepository activityTypesRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserActivityService userActivityService;

    private User jack;
    private User bobby;
    private User regular;
    private User admin;
    private Activity marathon;
    private Activity athletics;
    private Activity eggHunt;
    private Activity basketball;
    private Activity sleeping;
    private Location location;
    private Location newLocation;

    private String placeId = "ChIJh5Z3Fw4gLG0RM0dqdeIY1rE";

    private List<String> activityTypes = List.of("Air Sphering", "Airboarding");
    private List<String> postHashtags = List.of("#noice", "#leshgo");
    private List<String> putHashtags = List.of("#hot", "#nofilter");
    private ActivityRequest activityRequest;
    private ActivityRequest activityEditRequest;

    public ActivityTest() throws ParseException {
    }

    @BeforeEach
    void setup() {
        activityRepository.deleteAll();
        userRepository.deleteAll();
        hashtagRepository.deleteAll();

        jack = new User("Jack", passwordEncoder.encode("password1"));
        bobby = new User("Bobby", passwordEncoder.encode("passwnnord1"));
        regular = new User("Regular guy", passwordEncoder.encode("passmerd1"));
        admin = new User("Admin", passwordEncoder.encode("passwnnord12"));
        admin.setIsAdmin(true);
        userRepository.saveAll(List.of(jack, bobby, admin, regular));

        ActivityType running = activityTypesRepository.getActivityTypesFromName("Running");
        List<ActivityType> activityActivityTypes = List.of(running);
        Set<Hashtag> startingHashtags = Set.of(new Hashtag("#yes"), new Hashtag("#no"));
        hashtagRepository.saveAll(startingHashtags);

        location = new Location();
        location.setPlaceId("placeId");

        newLocation = new Location();
        newLocation.setPlaceId("newPlaceId");


        marathon = new Activity("marathon", "running alot", location, false, startDate, endDate, creationDate, ActivityVisibility.PUBLIC);
        marathon.setHashtags(startingHashtags);
        marathon.setActivityTypes(activityActivityTypes);
        marathon.setCreator(jack);
        marathon.getOrganisers().add(bobby);

        athletics = new Activity("athletics", "running alot", location, false, startDate, endDate, creationDate, ActivityVisibility.PUBLIC);
        athletics.setCreator(jack);

        eggHunt =  new Activity("egg hunt", "eating alot", location, false, startDate, endDate, creationDate, ActivityVisibility.PUBLIC);
        eggHunt.getFollowers().add(jack);

        basketball =  new Activity("basketball", "shooting alot", location, false, startDate, endDate, creationDate, ActivityVisibility.PUBLIC);
        basketball.getOrganisers().add(jack);

        sleeping =  new Activity("sleeping", "snoring alot", location, false, startDate, endDate, creationDate, ActivityVisibility.PUBLIC);
        sleeping.getParticipants().add(jack);

        activityRepository.saveAll(List.of(marathon, athletics, eggHunt, basketball, sleeping));

        activityRequest = new ActivityRequest("fun run", "(not actually fun)", activityTypes,
                false, startDate, endDate, location, postHashtags, ActivityVisibility.PUBLIC);

        activityEditRequest = new ActivityRequest("Triathlon", "Some more running", activityTypes,
                true, startDate, endDate, newLocation, putHashtags, ActivityVisibility.PUBLIC);
    }

    @Test
    void testPostActivityShouldHaveCorrectCreator() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/profiles/" + jack.getId() + "/activities")
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(jack)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(activityRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        Long activityId = Long.parseLong(response);

        Activity activity = userActivityService.readByActivityId(activityId);
        assertEquals(activity.getCreator().getId(), jack.getId());
    }

    @Test
    void testPostActivityShouldHaveCorrectActivityName() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/profiles/" + jack.getId() + "/activities")
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(jack)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(activityRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        Long activityId = Long.parseLong(response);

        Activity activity = userActivityService.readByActivityId(activityId);
        assertEquals(activity.getActivityName(), activityRequest.getActivityName());
    }

    @Test
    void testPostActivityShouldHaveCorrectLocation() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/profiles/" + jack.getId() + "/activities")
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(jack)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(activityRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        Long activityId = Long.parseLong(response);

        Activity activity = userActivityService.readByActivityId(activityId);
        assertEquals(activity.getLocation().getPlaceId(), activityRequest.getLocation().getPlaceId());
    }

    @Test
    void testPostActivityShouldHaveCorrectContinuousValue() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/profiles/" + jack.getId() + "/activities")
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(jack)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(activityRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        Long activityId = Long.parseLong(response);

        Activity activity = userActivityService.readByActivityId(activityId);
        assertEquals(activity.isContinuous(), activityRequest.isContinuous());
    }

    @Test
    void testPostActivityShouldHaveCorrectStartDate() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/profiles/" + jack.getId() + "/activities")
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(jack)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(activityRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        Long activityId = Long.parseLong(response);

        Activity activity = userActivityService.readByActivityId(activityId);
        assertEquals(activity.getStartDate().getTime(), activityRequest.getStartDate().getTime());
    }

    @Test
    void testPostActivityShouldHaveCorrectEndDate() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/profiles/" + jack.getId() + "/activities")
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(jack)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(activityRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        Long activityId = Long.parseLong(response);

        Activity activity = userActivityService.readByActivityId(activityId);
        assertEquals(activity.getEndDate().getTime(), activityRequest.getEndDate().getTime());
    }

    @Test
    void testPostActivityShouldHaveCorrectActivityTypes() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/profiles/" + jack.getId() + "/activities")
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(jack)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(activityRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        Long activityId = Long.parseLong(response);

        Activity activity = userActivityService.readByActivityId(activityId);
        List<String> activityTypes = activity.getActivityTypes()
                .stream()
                .map(ActivityType::getType)
                .collect(Collectors.toList());
        assertTrue(activityTypes.contains("Air Sphering"));
        assertTrue(activityTypes.contains("Airboarding"));
    }

    @Test
    void testPostActivityShouldHaveCorrectDescription() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/profiles/" + jack.getId() + "/activities")
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(jack)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(activityRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        Long activityId = Long.parseLong(response);

        Activity activity = userActivityService.readByActivityId(activityId);
        assertEquals(activity.getDescription(), activityRequest.getDescription());
    }

    @Test
    void testEndDateFarInFutureShouldReturnBadRequest() throws Exception {
        activityRequest.setContinuous(false);
        activityRequest.setStartDate(formatter.parse("2020-11-20T08:00:00+1300"));
        activityRequest.setEndDate(formatter.parse("2050-11-20T08:00:00+1300"));
        mockMvc.perform(post("/profiles/" + jack.getId() + "/activities/")
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(jack)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(activityRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testHashtagAddedToDatabaseOnlyOnceAfterPost() throws Exception {
        var startingHashtagSize = hashtagRepository.findAll().size();
        mockMvc.perform(post("/profiles/" + jack.getId() + "/activities/")
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(jack)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(activityRequest)))
                .andExpect(status().isCreated());
        assertEquals(startingHashtagSize + 2, hashtagRepository.findAll().size());
        mockMvc.perform(post("/profiles/" + jack.getId() + "/activities/")
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(jack)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(activityRequest)))
                .andExpect(status().isCreated());
        assertEquals(startingHashtagSize + 2, hashtagRepository.findAll().size());
    }

    @Test
    void testHashtagAddedToDatabaseOnlyOnceAfterPut() throws Exception {
        var startingHashtagSize = hashtagRepository.findAll().size();
        mockMvc.perform(put("/profiles/" + jack.getId() + "/activities/" + marathon.getId())
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(jack)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(activityEditRequest)))
                .andExpect(status().isOk());
        assertEquals(startingHashtagSize + 2, hashtagRepository.findAll().size());
    }

    @Test
    void testStartDateFarInFutureShouldReturnBadRequest() throws Exception {
        activityRequest.setContinuous(false);
        activityRequest.setStartDate(formatter.parse("2050-11-20T08:00:00+1300"));
        activityRequest.setEndDate(formatter.parse("2021-11-20T08:00:00+1300"));
        mockMvc.perform(post("/profiles/" + jack.getId() + "/activities/")
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(jack)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(activityRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testPutActivityFromCreatorShouldChangeName() throws Exception {
        marathon = userActivityService.readByActivityId(marathon.getId());
        assertEquals("marathon", marathon.getActivityName());

        mockMvc.perform(put("/profiles/" + jack.getId() + "/activities/" + marathon.getId())
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(jack)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(activityEditRequest)))
                .andExpect(status().isOk());

        marathon = userActivityService.readByActivityId(marathon.getId());
        assertEquals("Triathlon", marathon.getActivityName());
    }

    @Test
    void testPutActivityFromCreatorShouldChangeDescription() throws Exception {
        marathon = userActivityService.readByActivityId(marathon.getId());
        assertEquals("running alot", marathon.getDescription());

        mockMvc.perform(put("/profiles/" + jack.getId() + "/activities/" + marathon.getId())
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(jack)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(activityEditRequest)))
                .andExpect(status().isOk());

        marathon = userActivityService.readByActivityId(marathon.getId());
        assertEquals("Some more running", marathon.getDescription());
    }

    @Test
    void testPutActivityFromCreatorShouldChangeLocation() throws Exception {
        marathon = userActivityService.readByActivityId(marathon.getId());
        assertEquals(location.getPlaceId(), marathon.getLocation().getPlaceId());

        mockMvc.perform(put("/profiles/" + jack.getId() + "/activities/" + marathon.getId())
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(jack)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(activityEditRequest)))
                .andExpect(status().isOk());

        marathon = userActivityService.readByActivityId(marathon.getId());
        assertEquals(newLocation.getPlaceId(), marathon.getLocation().getPlaceId());
    }

    @Test
    void testPutActivityFromCreatorShouldChangeContinuousValue() throws Exception {
        marathon = userActivityService.readByActivityId(marathon.getId());
        assertFalse(marathon.isContinuous());

        mockMvc.perform(put("/profiles/" + jack.getId() + "/activities/" + marathon.getId())
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(jack)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(activityEditRequest)))
                .andExpect(status().isOk());

        marathon = userActivityService.readByActivityId(marathon.getId());
        assertTrue(marathon.isContinuous());
    }

    @Test
    void testPutActivityFromCreatorShouldChangeStartDate() throws Exception {
        marathon = userActivityService.readByActivityId(marathon.getId());
        assertEquals("marathon", marathon.getActivityName());

        mockMvc.perform(put("/profiles/" + jack.getId() + "/activities/" + marathon.getId())
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(jack)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(activityEditRequest)))
                .andExpect(status().isOk());

        marathon = userActivityService.readByActivityId(marathon.getId());
        assertEquals(activityEditRequest.getStartDate().getTime(), marathon.getStartDate().getTime());
    }

    @Test
    void testPutActivityFromCreatorShouldChangeEndDate() throws Exception {
        marathon = userActivityService.readByActivityId(marathon.getId());
        assertEquals("marathon", marathon.getActivityName());

        mockMvc.perform(put("/profiles/" + jack.getId() + "/activities/" + marathon.getId())
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(jack)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(activityEditRequest)))
                .andExpect(status().isOk());

        marathon = userActivityService.readByActivityId(marathon.getId());
        assertEquals(activityEditRequest.getEndDate().getTime(), marathon.getEndDate().getTime());
    }

    @Test
    void testPutActivityFromAdminShouldSucceed() throws Exception {
        marathon = userActivityService.readByActivityId(marathon.getId());
        assertEquals("marathon", marathon.getActivityName());
        mockMvc.perform(put("/profiles/" + jack.getId() + "/activities/" + marathon.getId())
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(admin)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(activityEditRequest)))
                .andExpect(status().isOk());

        marathon = userActivityService.readByActivityId(marathon.getId());
        assertEquals("Triathlon", marathon.getActivityName());
    }

    @Test
    void testPutActivityFromNonCreator() throws Exception {
        mockMvc.perform(put("/profiles/" + regular.getId() + "/activities/" + marathon.getId())
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(regular)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(activityEditRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testNameIsNull() throws Exception {
        activityEditRequest.setActivityName(null);
        mockMvc.perform(put("/profiles/" + jack.getId() + "/activities/" + marathon.getId())
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(jack)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(activityEditRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testNameIsBlank() throws Exception {
        activityEditRequest.setActivityName("");
        mockMvc.perform(put("/profiles/" + jack.getId() + "/activities/" + marathon.getId())
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(jack)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(activityEditRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testNameIsTooLong() throws Exception {
        activityEditRequest.setActivityName("Uvuvwevwevwe Onyetenyevwe Ugwemubwem Ossas Uvuvwevwevwe Onyetenyevwe " +
                "Ugwemubwem Ossas Uvuvwevwevwe Onyetenyevwe Ugwemubwem Ossas ");
        mockMvc.perform(put("/profiles/" + jack.getId() + "/activities/" + marathon.getId())
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(jack)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(activityEditRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDescriptionCanBeEmpty() throws Exception {
        activityEditRequest.setDescription("");
        mockMvc.perform(put("/profiles/" + jack.getId() + "/activities/" + marathon.getId())
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(jack)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(activityEditRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void testStartDateInThePast() throws Exception {
        activityEditRequest.setContinuous(false);
        activityEditRequest.setStartDate(formatter.parse("1999-11-20T08:00:00+1300"));
        mockMvc.perform(put("/profiles/" + jack.getId() + "/activities/" + marathon.getId())
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(jack)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(activityEditRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testStartDateCanBeNullWhenTimeRangeIsContinuous() throws Exception {
        activityEditRequest.setContinuous(true);
        activityEditRequest.setStartDate(null);
        mockMvc.perform(put("/profiles/" + jack.getId() + "/activities/" + marathon.getId())
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(jack)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(activityEditRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void testEndDateInThePast() throws Exception {
        activityEditRequest.setContinuous(false);
        activityEditRequest.setEndDate(formatter.parse("1999-11-20T08:00:00+1300"));
        mockMvc.perform(put("/profiles/" + jack.getId() + "/activities/" + marathon.getId())
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(jack)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(activityEditRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testEndDateCanBeNullWhenTimeRangeIsContinuous() throws Exception {
        activityEditRequest.setContinuous(true);
        activityEditRequest.setEndDate(null);
        mockMvc.perform(put("/profiles/" + jack.getId() + "/activities/" + marathon.getId())
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(jack)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(activityEditRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void testEndDateBeforeStartDate() throws Exception {
        activityEditRequest.setContinuous(false);
        activityEditRequest.setStartDate(formatter.parse("2020-11-20T08:00:00+1300"));
        activityEditRequest.setEndDate(formatter.parse("2008-11-20T08:00:00+1300"));
        mockMvc.perform(put("/profiles/" + jack.getId() + "/activities/" + marathon.getId())
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(jack)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(activityEditRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLocationIsEmpty() throws Exception {
        activityEditRequest.setLocation(null);
        mockMvc.perform(put("/profiles/" + jack.getId() + "/activities/" + marathon.getId())
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(jack)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(activityEditRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testActivityTypesIsNull() throws Exception {
        activityEditRequest.setActivityTypes(null);
        mockMvc.perform(put("/profiles/" + jack.getId() + "/activities/" + marathon.getId())
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(jack)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(activityEditRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testActivityTypesIsEmpty() throws Exception {
        List<String> emptyActivityTypes = List.of(); // empty list!
        activityEditRequest.setActivityTypes(emptyActivityTypes);
        mockMvc.perform(put("/profiles/" + jack.getId() + "/activities/" + marathon.getId())
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(jack)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(activityEditRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testBadNotRegisteredActivityTypes() throws Exception {
        List<String> emptyActivityTypes = List.of("playing league - should be one", "sleeping");
        activityEditRequest.setActivityTypes(emptyActivityTypes);
        mockMvc.perform(put("/profiles/" + jack.getId() + "/activities/" + marathon.getId())
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(jack)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(activityEditRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteActivityFromCreator() throws Exception {
        mockMvc.perform(delete("/profiles/" + jack.getId() + "/activities/" + marathon.getId())
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(jack))))
                .andExpect(status().isOk());
        assertTrue(
                activityRepository.findByActivityName(activityRequest.getActivityName()).isEmpty()
        );
    }

    @Test
    void testDeleteActivityFromOrganiserShouldFail() throws Exception {
        mockMvc.perform(delete("/profiles/" + jack.getId() + "/activities/" + marathon.getId())
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(bobby))))
                .andExpect(status().isForbidden());
    }

    @Test
    void testDeleteActivityFromNonCreator() throws Exception {
        mockMvc.perform(delete("/profiles/" + jack.getId() + "/activities/" + marathon.getId())
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(regular))))
                .andExpect(status().isForbidden());
    }


    @Test
    void testDeleteActivityFromAdmin() throws Exception {
        mockMvc.perform(delete("/profiles/" + jack.getId() + "/activities/" + marathon.getId())
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(admin))))
                .andExpect(status().isOk());
        assertTrue(
                activityRepository.findByActivityName(activityRequest.getActivityName()).isEmpty()
        );
    }

    @Test
    void testDeleteActivityTwice() throws Exception {
        mockMvc.perform(delete("/profiles/" + jack.getId() + "/activities/" + marathon.getId())
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(jack))))
                .andExpect(status().isOk());

        // If the same activity is deleted again...
        mockMvc.perform(delete("/profiles/" + jack.getId() + "/activities/" + marathon.getId())
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(jack))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetActivitiesFromUser() throws Exception {
        mockMvc.perform(get("/profiles/" + jack.getId() + "/activities")
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(jack))))
                .andExpect(status().isOk());
    }

    @Test
    void testGetActivitiesFromOtherUser() throws Exception {
        mockMvc.perform(get("/profiles/" + jack.getId() + "/activities")
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(bobby))))
                .andExpect(status().isOk());
    }

    @Test
    void testGetActivitiesCreatedByUser() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/profiles/" + jack.getId() + "/activities/created")
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(jack))))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertEquals("marathon", JsonPath.parse(response).read("$[0].activity_name"));
        assertEquals("athletics", JsonPath.parse(response).read("$[1].activity_name"));
    }

    @Test
    void testGetActivitiesFromAdmin() throws Exception {
        mockMvc.perform(get("/profiles/" + jack.getId() + "/activities")
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(admin))))
                .andExpect(status().isOk());
    }

    @Test
    void testGetIndividualActivityById() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/activities/" + marathon.getId())
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(admin))))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertEquals("marathon", JsonPath.parse(response).read("$.activity_name"));

    }

    @Test
    void testGetAllActivitiesByUserId() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/profiles/" + jack.getId() + "/activities/all")
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(jack))))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertEquals("marathon", JsonPath.parse(response).read("$[0].activity_name"));
    }
}

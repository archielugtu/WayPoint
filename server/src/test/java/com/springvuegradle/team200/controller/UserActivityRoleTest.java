package com.springvuegradle.team200.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.springvuegradle.team200.dto.request.EditActivityRoleRequest;
import com.springvuegradle.team200.jwt.JwtTokenUtil;
import com.springvuegradle.team200.model.*;
import com.springvuegradle.team200.repository.ActivityRepository;
import com.springvuegradle.team200.repository.UserRepository;
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
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserActivityRoleTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private UserActivityService userActivityService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    private User user;
    private User organiser;
    private User creator;
    private User admin;
    private String userToken;
    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    private final Date startDate = formatter.parse("2020-11-20T08:00:00+1300");
    private final Date endDate = formatter.parse("2020-12-20T08:00:00+1300");
    private final Date creationDate = formatter.parse("2020-01-01T08:00:00+1300");
    private Location location;
    private Activity activity;

    public UserActivityRoleTest() throws ParseException {

    }

    @BeforeEach
    void setup() {
        // Ensure the relevant database tables don't contain data that would interfere before beginning
        userRepository.deleteAll();
        activityRepository.deleteAll();

        location = new Location("ChIJAe3FY0gvMm0RRZl5hIbvAAU", "Christchurch", (float) -43.753797, (float) 172.777858, true);
        user = new User("RoleTestUser", passwordEncoder.encode("password"));
        user.setLocation(location);
        organiser = new User("RoleTestOrganiser", passwordEncoder.encode("password"));
        organiser.setLocation(location);
        creator = new User("RoleTestCreator", passwordEncoder.encode("password"));
        creator.setLocation(location);
        admin = new User("RoleTestAdmin", passwordEncoder.encode("password"));
        admin.setLocation(location);
        activity = new Activity("RoleTest", "Test", location, false,
                startDate, endDate, creationDate, ActivityVisibility.PUBLIC);

        admin.setIsAdmin(true);
        userRepository.saveAll(List.of(user, organiser, creator, admin));
        activity.setCreator(creator);
        activity.setOrganisers(new HashSet<>(Collections.singletonList(organiser)));
        activityRepository.save(activity);
        userToken = jwtTokenUtil.generateToken(creator);
    }

    @Test
    void testAddNewFollowerShouldSucceed() throws Exception {
        EditActivityRoleRequest editActivityRoleRequest = new EditActivityRoleRequest(ActivityRole.FOLLOWER);
        mockMvc.perform(post(String.format("/profiles/%d/subscriptions/activities/%d", user.getId(), activity.getId()))
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(user)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(editActivityRoleRequest)))
                .andExpect(status().isOk());

        Activity updatedActivity = userActivityService.readByActivityId(activity.getId());
        assertTrue(updatedActivity.getFollowers().contains(user));
        assertFalse(updatedActivity.getParticipants().contains(user));
        assertFalse(updatedActivity.getOrganisers().contains(user));
        assertNotEquals(updatedActivity.getCreator(), user);
    }

    @Test
    void testAddNewParticipantShouldSucceed() throws Exception {
        EditActivityRoleRequest editActivityRoleRequest = new EditActivityRoleRequest(ActivityRole.PARTICIPANT);
        mockMvc.perform(post(String.format("/profiles/%d/subscriptions/activities/%d", user.getId(), activity.getId()))
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(user)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(editActivityRoleRequest)))
                .andExpect(status().isOk());

        Activity updatedActivity = userActivityService.readByActivityId(activity.getId());
        assertTrue(updatedActivity.getParticipants().contains(user));

        assertFalse(updatedActivity.getFollowers().contains(user));
        assertFalse(updatedActivity.getOrganisers().contains(user));
        assertNotEquals(updatedActivity.getCreator(), user);
    }

    @Test
    void testAddSelfAsOrganiserShouldFail() throws Exception {
        EditActivityRoleRequest editActivityRoleRequest = new EditActivityRoleRequest(ActivityRole.ORGANISER);
        mockMvc.perform(post(String.format("/profiles/%d/subscriptions/activities/%d", user.getId(), activity.getId()))
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(user)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(editActivityRoleRequest)))
                .andExpect(status().isForbidden());

        Activity updatedActivity = userActivityService.readByActivityId(activity.getId());
        assertFalse(updatedActivity.getOrganisers().contains(user));

        assertFalse(updatedActivity.getFollowers().contains(user));
        assertFalse(updatedActivity.getParticipants().contains(user));
        assertNotEquals(updatedActivity.getCreator(), user);
    }

    @Test
    void testAddOrganiserAsOrganiserShouldSucceed() throws Exception {
        EditActivityRoleRequest editActivityRoleRequest = new EditActivityRoleRequest(ActivityRole.ORGANISER);
        mockMvc.perform(post(String.format("/profiles/%d/subscriptions/activities/%d", user.getId(), activity.getId()))
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(organiser)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(editActivityRoleRequest)))
                .andExpect(status().isOk());

        Activity updatedActivity = userActivityService.readByActivityId(activity.getId());
        assertTrue(updatedActivity.getOrganisers().contains(user));

        assertFalse(updatedActivity.getFollowers().contains(user));
        assertFalse(updatedActivity.getParticipants().contains(user));
        assertNotEquals(updatedActivity.getCreator(), user);
    }

    @Test
    void testAddOrganiserAsCreatorShouldSucceed() throws Exception {
        EditActivityRoleRequest editActivityRoleRequest = new EditActivityRoleRequest(ActivityRole.ORGANISER);
        mockMvc.perform(post(String.format("/profiles/%d/subscriptions/activities/%d", user.getId(), activity.getId()))
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(creator)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(editActivityRoleRequest)))
                .andExpect(status().isOk());

        Activity updatedActivity = userActivityService.readByActivityId(activity.getId());
        assertTrue(updatedActivity.getOrganisers().contains(user));

        assertFalse(updatedActivity.getFollowers().contains(user));
        assertFalse(updatedActivity.getParticipants().contains(user));
        assertNotEquals(updatedActivity.getCreator(), user);
    }

    @Test
    void testAddOrganiserAsAdminShouldSucceed() throws Exception {
        EditActivityRoleRequest editActivityRoleRequest = new EditActivityRoleRequest(ActivityRole.ORGANISER);
        mockMvc.perform(post(String.format("/profiles/%d/subscriptions/activities/%d", user.getId(), activity.getId()))
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(admin)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(editActivityRoleRequest)))
                .andExpect(status().isOk());

        Activity updatedActivity = userActivityService.readByActivityId(activity.getId());
        assertTrue(updatedActivity.getOrganisers().contains(user));

        assertFalse(updatedActivity.getFollowers().contains(user));
        assertFalse(updatedActivity.getParticipants().contains(user));
        assertNotEquals(updatedActivity.getCreator(), user);
    }

    @Test
    void testAddCreatorAsUserShouldFail() throws Exception {
        EditActivityRoleRequest editActivityRoleRequest = new EditActivityRoleRequest(ActivityRole.CREATOR);
        mockMvc.perform(post(String.format("/profiles/%d/subscriptions/activities/%d", user.getId(), activity.getId()))
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(user)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(editActivityRoleRequest)))
                .andExpect(status().isForbidden());

        Activity updatedActivity = userActivityService.readByActivityId(activity.getId());
        assertNotEquals(updatedActivity.getCreator(), user);
        assertEquals(updatedActivity.getCreator(), creator);

        assertFalse(updatedActivity.getFollowers().contains(user));
        assertFalse(updatedActivity.getParticipants().contains(user));
        assertFalse(updatedActivity.getOrganisers().contains(user));
    }

    @Test
    void testAddCreatorAsOrganiserShouldFail() throws Exception {
        EditActivityRoleRequest editActivityRoleRequest = new EditActivityRoleRequest(ActivityRole.CREATOR);
        mockMvc.perform(post(String.format("/profiles/%d/subscriptions/activities/%d", user.getId(), activity.getId()))
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(organiser)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(editActivityRoleRequest)))
                .andExpect(status().isForbidden());

        Activity updatedActivity = userActivityService.readByActivityId(activity.getId());
        assertNotEquals(updatedActivity.getCreator(), user);
        assertEquals(updatedActivity.getCreator(), creator);

        assertFalse(updatedActivity.getFollowers().contains(user));
        assertFalse(updatedActivity.getParticipants().contains(user));
        assertFalse(updatedActivity.getOrganisers().contains(user));
    }

    @Test
    void testAddCreatorAsCreatorShouldFail() throws Exception {
        EditActivityRoleRequest editActivityRoleRequest = new EditActivityRoleRequest(ActivityRole.CREATOR);
        mockMvc.perform(post(String.format("/profiles/%d/subscriptions/activities/%d", user.getId(), activity.getId()))
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(creator)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(editActivityRoleRequest)))
                .andExpect(status().isForbidden());

        Activity updatedActivity = userActivityService.readByActivityId(activity.getId());
        assertNotEquals(updatedActivity.getCreator(), user);
        assertEquals(updatedActivity.getCreator(), creator);

        assertFalse(updatedActivity.getFollowers().contains(user));
        assertFalse(updatedActivity.getParticipants().contains(user));
        assertFalse(updatedActivity.getOrganisers().contains(user));
    }

    @Test
    void testAddCreatorAsAdminShouldFail() throws Exception {
        EditActivityRoleRequest editActivityRoleRequest = new EditActivityRoleRequest(ActivityRole.CREATOR);
        mockMvc.perform(post(String.format("/profiles/%d/subscriptions/activities/%d", user.getId(), activity.getId()))
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(admin)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(editActivityRoleRequest)))
                .andExpect(status().isForbidden());

        Activity updatedActivity = userActivityService.readByActivityId(activity.getId());
        assertNotEquals(updatedActivity.getCreator(), user);
        assertEquals(updatedActivity.getCreator(), creator);

        assertFalse(updatedActivity.getFollowers().contains(user));
        assertFalse(updatedActivity.getParticipants().contains(user));
        assertFalse(updatedActivity.getOrganisers().contains(user));
    }

    @Test
    void testChangeRolesWhenCreatorShouldFail() throws Exception {
        EditActivityRoleRequest editActivityRoleRequest = new EditActivityRoleRequest(ActivityRole.FOLLOWER);
        mockMvc.perform(post(String.format("/profiles/%d/subscriptions/activities/%d", creator.getId(), activity.getId()))
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(creator)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(editActivityRoleRequest)))
                .andExpect(status().isForbidden());

        Activity updatedActivity = userActivityService.readByActivityId(activity.getId());
        assertEquals(updatedActivity.getCreator(), creator);

        assertFalse(updatedActivity.getFollowers().contains(user));
        assertFalse(updatedActivity.getParticipants().contains(user));
        assertFalse(updatedActivity.getOrganisers().contains(user));
    }

    @Test
    void testChangeRoleFromFollowerToParticipantShouldSucceed() throws Exception {
        activity.setFollowers(new HashSet<>(List.of(user)));
        activityRepository.save(activity);

        EditActivityRoleRequest editActivityRoleRequest = new EditActivityRoleRequest(ActivityRole.PARTICIPANT);
        mockMvc.perform(post(String.format("/profiles/%d/subscriptions/activities/%d", user.getId(), activity.getId()))
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(user)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(editActivityRoleRequest)))
                .andExpect(status().isOk());

        Activity updatedActivity = userActivityService.readByActivityId(activity.getId());
        assertFalse(updatedActivity.getFollowers().contains(user));
        assertTrue(updatedActivity.getParticipants().contains(user));
    }

    @Test
    void testChangeRoleFromParticipantToFollowerShouldSucceed() throws Exception {
        activity.setParticipants(new HashSet<>(List.of(user)));
        activityRepository.save(activity);

        EditActivityRoleRequest editActivityRoleRequest = new EditActivityRoleRequest(ActivityRole.FOLLOWER);
        mockMvc.perform(post(String.format("/profiles/%d/subscriptions/activities/%d", user.getId(), activity.getId()))
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(user)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(editActivityRoleRequest)))
                .andExpect(status().isOk());

        Activity updatedActivity = userActivityService.readByActivityId(activity.getId());
        assertTrue(updatedActivity.getFollowers().contains(user));
        assertFalse(updatedActivity.getParticipants().contains(user));
    }

    @Test
    void testChangeRoleFromFollowerToOrganiserAsUserShouldFail() throws Exception {
        activity.setFollowers(new HashSet<>(List.of(user)));
        activityRepository.save(activity);

        EditActivityRoleRequest editActivityRoleRequest = new EditActivityRoleRequest(ActivityRole.ORGANISER);
        mockMvc.perform(post(String.format("/profiles/%d/subscriptions/activities/%d", user.getId(), activity.getId()))
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(user)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(editActivityRoleRequest)))
                .andExpect(status().isForbidden());

        Activity updatedActivity = userActivityService.readByActivityId(activity.getId());
        assertTrue(updatedActivity.getFollowers().contains(user));
        assertFalse(updatedActivity.getOrganisers().contains(user));
    }

    @Test
    void testChangeRoleFromFollowerToOrganiserAsOrganiserShouldSucceed() throws Exception {
        activity.setFollowers(new HashSet<>(List.of(user)));
        activityRepository.save(activity);

        EditActivityRoleRequest editActivityRoleRequest = new EditActivityRoleRequest(ActivityRole.ORGANISER);
        mockMvc.perform(post(String.format("/profiles/%d/subscriptions/activities/%d", user.getId(), activity.getId()))
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(organiser)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(editActivityRoleRequest)))
                .andExpect(status().isOk());

        Activity updatedActivity = userActivityService.readByActivityId(activity.getId());
        assertTrue(updatedActivity.getOrganisers().contains(user));
        assertFalse(updatedActivity.getFollowers().contains(user));
    }

    @Test
    void testChangeRoleFromOrganiserToFollowerShouldSucceed() throws Exception {
        EditActivityRoleRequest editActivityRoleRequest = new EditActivityRoleRequest(ActivityRole.FOLLOWER);
        mockMvc.perform(post(String.format("/profiles/%d/subscriptions/activities/%d", organiser.getId(), activity.getId()))
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(organiser)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(editActivityRoleRequest)))
                .andExpect(status().isOk());

        Activity updatedActivity = userActivityService.readByActivityId(activity.getId());
        assertTrue(updatedActivity.getFollowers().contains(organiser));
        assertFalse(updatedActivity.getOrganisers().contains(organiser));
    }

    @Test
    void testDeleteUserRemovesActivityFollowerRole() throws Exception {
        EditActivityRoleRequest editActivityRoleRequest = new EditActivityRoleRequest(ActivityRole.FOLLOWER);
        mockMvc.perform(post(String.format("/profiles/%d/subscriptions/activities/%d", user.getId(), activity.getId()))
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(user)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(editActivityRoleRequest)))
                .andExpect(status().isOk());

        Activity updatedActivity = userActivityService.readByActivityId(activity.getId());
        assertTrue(updatedActivity.getFollowers().contains(user));
        assertEquals(1, updatedActivity.getFollowers().size());

        mockMvc.perform(delete("/profiles/" + user.getId())
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(user))))
                .andExpect(status().isOk());

        updatedActivity = userActivityService.readByActivityId(activity.getId());
        assertTrue(updatedActivity.getFollowers().isEmpty());
    }

    @Test
    void testDeleteUserRemovesActivityOrganiserRole() throws Exception {
        assertEquals(1, activity.getOrganisers().size());
        mockMvc.perform(delete("/profiles/" + organiser.getId())
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(organiser))))
                .andExpect(status().isOk());

        Activity updatedActivity = userActivityService.readByActivityId(activity.getId());
        assertTrue(updatedActivity.getOrganisers().isEmpty());
    }


    @Test
    void testRetrieveAllFollowers() throws Exception {
        user.setActivitiesFollowing(new HashSet<>(Collections.singletonList(activity)));
        userRepository.save(user);
        MvcResult mvcResult = mockMvc.perform(get("/activities/" + activity.getId() + "/followers")
                .cookie(new Cookie("token", userToken))
                .param("page", "0")
                .param("size", "5"))
                .andExpect(status().isOk())
                .andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        assertEquals(1, JsonPath.parse(response).read("$.total_users", Integer.class));
        assertEquals("Firstname Lastname", JsonPath.parse(response).read("$.followers[0].name"));
    }

    @Test
    void testRetrieveAllFollowersWhenNoFollowers() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/activities/" + activity.getId() + "/followers")
                .cookie(new Cookie("token", userToken))
                .param("page", "0")
                .param("size", "5"))
                .andExpect(status().isOk())
                .andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        assertEquals(0, JsonPath.parse(response).read("$.total_users", Integer.class));
    }
    @Test
    void testRetrieveAllOrganisers() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/activities/" + activity.getId() + "/organisers")
                .cookie(new Cookie("token", userToken))
                .param("page", "0")
                .param("size", "5"))
                .andExpect(status().isOk())
                .andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        assertEquals(1, JsonPath.parse(response).read("$.total_users", Integer.class));
        assertEquals("Firstname Lastname", JsonPath.parse(response).read("$.organisers[0].name"));
    }

    @Test
    void testRetrieveAllOrganisersWhenNoFollowers() throws Exception {
        Activity activity2 = new Activity("RoleTest", "Test", new Location(), false,
                startDate, endDate, creationDate, ActivityVisibility.PUBLIC);
        activityRepository.save(activity2);
        MvcResult mvcResult = mockMvc.perform(get("/activities/" + activity2.getId() + "/organisers")
                .cookie(new Cookie("token", userToken))
                .param("page", "0")
                .param("size", "5"))
                .andExpect(status().isOk())
                .andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        assertEquals(0, JsonPath.parse(response).read("$.total_users", Integer.class));
    }
    @Test
    void testRetrieveAllParticipants() throws Exception {
        user.setActivityParticipator(new HashSet<>(Collections.singletonList(activity)));
        userRepository.save(user);
        MvcResult mvcResult = mockMvc.perform(get("/activities/" + activity.getId() + "/participants")
                .cookie(new Cookie("token", userToken))
                .param("page", "0")
                .param("size", "5"))
                .andExpect(status().isOk())
                .andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        assertEquals(1, JsonPath.parse(response).read("$.total_users", Integer.class));
        assertEquals("Firstname Lastname", JsonPath.parse(response).read("$.participants[0].name"));
    }

    @Test
    void testRetrieveAllFollowersWhenNoParticipants() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/activities/" + activity.getId() + "/participants")
                .cookie(new Cookie("token", userToken))
                .param("page", "0")
                .param("size", "5"))
                .andExpect(status().isOk())
                .andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        assertEquals(0, JsonPath.parse(response).read("$.total_users", Integer.class));
    }

    @Test
    void testCreatorPromoteParticipatorRoleToOrganiser() throws Exception {
        EditActivityRoleRequest editActivityRoleRequest = new EditActivityRoleRequest(ActivityRole.ORGANISER);
        mockMvc.perform(post(String.format("/profiles/%d/subscriptions/activities/%d/%d", creator.getId(), activity.getId(), user.getId()))
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(creator)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(editActivityRoleRequest)))
                .andExpect(status().isOk());

        Activity updatedActivity = userActivityService.readByActivityId(activity.getId());
        assertTrue(updatedActivity.getOrganisers().contains(user));

        assertFalse(updatedActivity.getFollowers().contains(user));
        assertFalse(updatedActivity.getParticipants().contains(user));
        assertNotEquals(updatedActivity.getCreator(), user);
    }

    @Test
    void testCreatorDemoteParticipatorRoleToParticipator() throws Exception {
        EditActivityRoleRequest editActivityRoleRequest = new EditActivityRoleRequest(ActivityRole.PARTICIPANT);
        mockMvc.perform(post(String.format("/profiles/%d/subscriptions/activities/%d/%d", creator.getId(), activity.getId(), user.getId()))
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(creator)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(editActivityRoleRequest)))
                .andExpect(status().isOk());

        Activity updatedActivity = userActivityService.readByActivityId(activity.getId());
        assertTrue(updatedActivity.getParticipants().contains(user));

        assertFalse(updatedActivity.getFollowers().contains(user));
        assertFalse(updatedActivity.getOrganisers().contains(user));
        assertNotEquals(updatedActivity.getCreator(), user);
    }
}

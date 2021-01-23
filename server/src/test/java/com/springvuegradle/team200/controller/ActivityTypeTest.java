package com.springvuegradle.team200.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springvuegradle.team200.dto.request.ActivityTypeRequest;
import com.springvuegradle.team200.jwt.JwtTokenUtil;
import com.springvuegradle.team200.model.ActivityType;
import com.springvuegradle.team200.model.User;
import com.springvuegradle.team200.repository.ActivityTypesRepository;
import com.springvuegradle.team200.repository.UserRepository;
import com.springvuegradle.team200.service.UserService;
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

import javax.servlet.http.Cookie;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ActivityTypeTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ActivityTypesRepository activityRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserService userService;

    private String jackToken;
    private Long jackId;
    private List<ActivityType> activityTypes;
    private User jack;
    private User kevin;

    private List<String> BadActivityTypes = List.of("reading");
    private List<String> ValidActivityTypes = List.of("Camping");
    private ActivityTypeRequest InvalidActivityTypeRequest = new ActivityTypeRequest(BadActivityTypes);
    private ActivityTypeRequest ValidActivityTypeRequest = new ActivityTypeRequest(ValidActivityTypes);
    private ActivityTypeRequest NullActivityTypeRequest = new ActivityTypeRequest(null);

    @BeforeEach
    void setup() throws Exception {
        ActivityType Basketball = activityRepository.getActivityTypesFromName("Basketball");
        ActivityType Biathlon = activityRepository.getActivityTypesFromName("Biathlon");
        activityTypes = new ArrayList<>();
        activityTypes.add(Basketball);
        activityTypes.add(Biathlon);
        userRepository.deleteAll();
        kevin = new User("kevin", passwordEncoder.encode("kevinator1"));
        jack = new User("jack", passwordEncoder.encode("password1"));
        jack.setActivityTypes(activityTypes);
        userRepository.save(kevin);
        userRepository.save(jack);
        jackToken = jwtTokenUtil.generateToken(jack);
        jackId = jack.getId();
    }

    @Test
    void testGetAllActivitiesWithoutAuthShouldReturnOK() throws Exception {
        List<String> allActivityTypes = activityRepository.getAllActivityTypes();
        mockMvc.perform(get("/activities/types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(allActivityTypes))
                .andReturn();
    }

    @Test
    void testGetUserActivities() throws Exception {
        ArrayList<String> testArrayList = new ArrayList<>();
        testArrayList.add("Basketball");
        testArrayList.add("Biathlon");
        mockMvc.perform(get("/profiles/" + jackId + "/activity-types")
                .cookie(new Cookie("token", jackToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(testArrayList))
                .andReturn();
    }

    @Test
    void testPutBadActivityType() throws Exception {
        mockMvc.perform(put("/profiles/" + jackId + "/activity-types")
                .cookie(new Cookie("token", jackToken))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(InvalidActivityTypeRequest)))
                .andExpect(status().isBadRequest())
                .andReturn();

        jack = userService.read(jackId);
        List<String> jackActivities = jack.getActivityTypes().stream()
                .map(ActivityType::getType)
                .collect(Collectors.toList());
        assertTrue(jackActivities.contains("Basketball"));
        assertTrue(jackActivities.contains("Biathlon"));
    }

    @Test
    void testPutValidActivityType() throws Exception {
        mockMvc.perform(put("/profiles/" + jackId + "/activity-types")
                .cookie(new Cookie("token", jackToken))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(ValidActivityTypeRequest)))
                .andExpect(status().isOk())
                .andReturn();

        jack = userService.read(jackId);
        List<String> jackActivities = jack.getActivityTypes().stream()
                .map(ActivityType::getType)
                .collect(Collectors.toList());
        assertTrue(jackActivities.contains("Camping"));
        assertEquals(1, jackActivities.size());
    }

    @Test
    void testActivityTypeNull() throws Exception {
        mockMvc.perform(put("/profiles/" + jackId + "/activity-types")
                .cookie(new Cookie("token", jackToken))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(NullActivityTypeRequest)))
                .andExpect(status().isBadRequest())
                .andReturn();

        jack = userService.read(jackId);
        List<String> jackActivities = jack.getActivityTypes().stream()
                .map(ActivityType::getType)
                .collect(Collectors.toList());
        assertTrue(jackActivities.contains("Basketball"));
        assertTrue(jackActivities.contains("Biathlon"));
    }

    @Test
    void testPutForbidden() throws Exception {
        mockMvc.perform(put("/profiles/" + kevin.getId() + "/activity-types")
                .cookie(new Cookie("token", jackToken))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(ValidActivityTypeRequest)))
                .andExpect(status().isForbidden())
                .andReturn();

        jack = userService.read(jackId);
        List<String> jackActivities = jack.getActivityTypes().stream()
                .map(ActivityType::getType)
                .collect(Collectors.toList());
        assertTrue(jackActivities.contains("Basketball"));
        assertTrue(jackActivities.contains("Biathlon"));
    }
}

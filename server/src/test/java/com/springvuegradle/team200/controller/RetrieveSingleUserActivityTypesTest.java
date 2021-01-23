package com.springvuegradle.team200.controller;

import com.jayway.jsonpath.JsonPath;
import com.springvuegradle.team200.jwt.JwtTokenUtil;
import com.springvuegradle.team200.model.ActivityType;
import com.springvuegradle.team200.model.User;
import com.springvuegradle.team200.repository.ActivityTypesRepository;
import com.springvuegradle.team200.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.servlet.http.Cookie;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RetrieveSingleUserActivityTypesTest {

    @Autowired
    ActivityTypesRepository activityTypesRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    private User bob;
    private String bobToken;
    private List<ActivityType> activityTypes;

    @BeforeEach
    void setupEach() {
        userRepository.deleteAll();

        bob = new User();
        userRepository.save(bob);
        bobToken = jwtTokenUtil.generateToken(bob);
    }

    @Test
    void testSingleActivityTypeShouldReturnOneActivityType() throws Exception {
        activityTypes = new ArrayList<>();
        ActivityType activityType = activityTypesRepository
                .findByType("Quad Biking").get();
        activityTypes.add(activityType);

        bob.setActivityTypes(activityTypes);
        userRepository.save(bob);

        MvcResult mvcResult = mockMvc.perform(get("/profiles/" + bob.getId() + "/activity-types")
                .cookie(new Cookie("token", bobToken)))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertEquals("Quad Biking", JsonPath.parse(response).read("$[0]"));
    }

    @Test
    void testEmptyActivityTypeShouldReturnNothing() throws Exception {
        activityTypes = new ArrayList<>();

        bob.setActivityTypes(activityTypes);
        userRepository.save(bob);

        MvcResult mvcResult = mockMvc.perform(get("/profiles/" + bob.getId() + "/activity-types")
                .cookie(new Cookie("token", bobToken)))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertEquals("[]", response);
    }

    @Test
    void testRetrieveNonExistingUserShouldReturn404() throws Exception {
        mockMvc.perform(get("/profiles/" + -1)
                .cookie(new Cookie("token", bobToken)))
                .andExpect(status().isNotFound());
    }
}

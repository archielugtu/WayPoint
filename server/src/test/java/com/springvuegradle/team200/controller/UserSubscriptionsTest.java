package com.springvuegradle.team200.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springvuegradle.team200.jwt.JwtTokenUtil;
import com.springvuegradle.team200.model.Activity;
import com.springvuegradle.team200.model.ActivityVisibility;
import com.springvuegradle.team200.model.Location;
import com.springvuegradle.team200.model.User;
import com.springvuegradle.team200.repository.ActivityRepository;
import com.springvuegradle.team200.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.Cookie;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserSubscriptionsTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    Date startDate = formatter.parse("2020-11-20T08:00:00+1300");
    Date endDate = formatter.parse("2020-12-20T08:00:00+1300");
    Date creationDate = formatter.parse("2020-01-01T08:00:00+1300");

    private User rchi;
    private User jerome;
    private Long rchiId;
    private Long jeromeId;

    private String rchiToken;
    private String rchiShortToken;

    private String jeromeToken;

    private Location location;
    private Location newLocation;

    private Activity marathon;
    private Activity athletics;
    private Activity easterEggHunt;

    private Long marathodId;
    private Long athleticsId;
    private Long easterEggHuntId;

    UserSubscriptionsTest() throws ParseException {
    }

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
        activityRepository.deleteAll();

        marathon = new Activity("marathon", "running alot", location, false, startDate, endDate, creationDate, ActivityVisibility.PUBLIC);
        athletics = new Activity("athletics", "doing alot", location, false, startDate, endDate, creationDate, ActivityVisibility.PUBLIC);
        easterEggHunt = new Activity("easterEggHunt", "eating alot", location, false, startDate, endDate, creationDate, ActivityVisibility.PUBLIC);
        activityRepository.saveAll(List.of(marathon, athletics, easterEggHunt));

        rchi = new User("Jack", passwordEncoder.encode("password1"));
        rchi.setActivitiesFollowing(Set.of(marathon, athletics, easterEggHunt));
        jerome = new User("Jerome", passwordEncoder.encode("password10"));

        userRepository.saveAll(List.of(rchi, jerome));

        rchiId = rchi.getId();
        rchiToken = jwtTokenUtil.generateToken(rchi);
        rchiShortToken = jwtTokenUtil.generateToken(rchi, 1L);

        jeromeId = jerome.getId();
        jeromeToken = jwtTokenUtil.generateToken(jerome);

        marathodId = marathon.getId();
        athleticsId = athletics.getId();
        easterEggHuntId = easterEggHunt.getId();

        userRepository.saveAll(List.of(rchi, jerome));
    }

    @Test
    void testUserFollowingAnActivity() throws Exception {
        mockMvc.perform(get("/profiles/" + rchiId + "/subscriptions/activities/" + marathodId)
                .cookie(new Cookie("token", rchiToken)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.subscribed").value(true));
    }

    @Test
    void testUserNotFollowingAnActivity() throws Exception {
        mockMvc.perform(get("/profiles/" + jeromeId + "/subscriptions/activities/" + athleticsId)
                .cookie(new Cookie("token", jeromeToken)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.subscribed").value(false));
    }
}

package com.springvuegradle.team200.controller;

import com.springvuegradle.team200.jwt.JwtTokenUtil;
import com.springvuegradle.team200.model.*;
import com.springvuegradle.team200.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.Cookie;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DeleteUserTest {

    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    private final List<String> jackEmailStrings = List.of("Jack@swag.com", "Jack@cool.com", "Jack@yeet.com");
    private final List<String> jackPassports = List.of("Afghanistan", "Sudan");
    private final List<String> longListOfEmailStrings = List.of(
            "abc@yeet.com", "abc@yeet2.com", "abc@yeet3.com",
            "abc@yeet4.com", "abc@yeet5.com", "abc@yeet6.com"
    );

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenUtil tokenUtil;

    @Autowired
    private ActivityTypesRepository activityTypesRepository;

    @Autowired
    private UserEmailRepository userEmailRepository;

    private String jackToken;
    private Long jackId;
    private Activity marathon;
    private String marathonName = "marathon";
    private Location location;
    private Location newLocation;

    @BeforeEach
    void setup() throws Exception {
        userRepository.deleteAll();
        activityRepository.deleteAll();
        userEmailRepository.deleteAll();
        activityRepository.deleteAll();

        location = new Location();
        location.setPlaceId("placeId");

        newLocation = new Location();
        newLocation.setPlaceId("newPlaceId");

        User jack = new User();

        setupSingleUser(jack, "Jack", "Craig", List.of("Running"));
        jackId = jack.getId();
        jackToken = tokenUtil.generateToken(jack);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        Date startDate = formatter.parse("2020-11-20T08:00:00+1300");
        Date endDate = formatter.parse("2020-12-20T08:00:00+1300");
        Date creationDate = formatter.parse("2020-01-01T08:00:00+1300");

        List<ActivityType> activityActivityTypes = List.of(activityTypesRepository.getActivityTypesFromName("Running"));
        marathon = new Activity(marathonName, "running alot", location, false, startDate, endDate, creationDate, ActivityVisibility.PUBLIC);
        marathon.setActivityTypes(activityActivityTypes);
        marathon.setCreator(jack);
        activityRepository.save(marathon);
    }

    void setupSingleUser(User user, String name, String lName, List<String> activityTypes) {
        List<ActivityType> activityTypeList = new ArrayList<>();
        for (String s : activityTypes) {
            activityTypeList.add(activityTypesRepository.findByType(s).get());
        }
        user.setActivityTypes(activityTypeList);
        user.setFirstName(name);
        user.setLastName(lName);
        user.setUserEmails(List.of(new UserEmail(name + "@" + name + ".com", user, true)));
        userRepository.save(user);
    }

    @Test
    void testDeleteUserRemovesUser() throws Exception {
        mockMvc.perform(delete("/profiles/" + jackId)
                .cookie(new Cookie("token", jackToken)))
                .andExpect(status().isOk());
        Optional<User> maybeJack = userRepository.findById(jackId);
        assertFalse(maybeJack.isPresent());
    }

    @Test
    void testDeleteUserRemovesEmails() throws Exception {
        List<String> existingEmailStrings = userEmailRepository.findByUser_Id(jackId).stream()
                .map(UserEmail::getAddress)
                .collect(Collectors.toList());
        for (var emailString : existingEmailStrings) {
            assertTrue(userEmailRepository.findFirstByAddress(emailString).isPresent());
        }
        mockMvc.perform(delete("/profiles/" + jackId)
                .cookie(new Cookie("token", jackToken)))
                .andExpect(status().isOk());
        for (var emailString : existingEmailStrings) {
            assertFalse(userEmailRepository.findFirstByAddress(emailString).isPresent());
        }
    }

    @Test
    void testDeleteUserDoesNotRemoveActivity() throws Exception {
        assertTrue(activityRepository.findByActivityName(marathonName).isPresent());
        mockMvc.perform(delete("/profiles/" + jackId)
                .cookie(new Cookie("token", jackToken)))
                .andExpect(status().isOk());
        assertTrue(activityRepository.findByActivityName(marathonName).isPresent());
    }
}

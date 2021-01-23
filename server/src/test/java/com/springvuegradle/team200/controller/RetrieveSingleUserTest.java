package com.springvuegradle.team200.controller;

import com.jayway.jsonpath.JsonPath;
import com.springvuegradle.team200.jwt.JwtTokenUtil;
import com.springvuegradle.team200.model.*;
import com.springvuegradle.team200.repository.ActivityTypesRepository;
import com.springvuegradle.team200.repository.UserPassportRepository;
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
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RetrieveSingleUserTest {

    private static final String firstName = "Bob";
    private static final String middleName = "The";
    private static final String lastName = "Builder";
    private static final String nickname = "BobTheBuilder";
    private static final String bio = "Loves building stuff";
    private static final String gender = "male";
    private static final String email = "bob@builder.com";
    private static final String emailSecondary = "bob@secondary.com";
    private static final String dob = "1970-01-01";
    private static final String passport = "India";
    private static final int fitness = 2;
    private static final String locationPlaceId = "placeId";
    private Location location;
    private Location newLocation;



    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserPassportRepository userPassportRepository;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private ActivityTypesRepository activityTypesRepository;


    private User bob;
    private String bobToken;
    private String fredToken;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();

        bob = new User();

        List<UserEmail> userEmails = new ArrayList<>();
        userEmails.add(new UserEmail(email, bob, true));
        userEmails.add(new UserEmail(emailSecondary, bob, false));

        List<PassportCountry> passportCountries = new ArrayList<>();
        if (userPassportRepository.findByName(passport) == null) {
            userPassportRepository.save(new PassportCountry("id", passport, "flag"));
        }
        passportCountries.add(userPassportRepository.findByName(passport));

        List<ActivityType> activityTypes = new ArrayList<>();
        ActivityType activityType = activityTypesRepository
                .findByType("Quad Biking").get();
        activityTypes.add(activityType);

        location = new Location();
        location.setPlaceId("placeId");

        newLocation = new Location();
        newLocation.setPlaceId("newPlaceId");


        // Bob initial setup
        bob.setFirstName(firstName);
        bob.setMiddleName(middleName);
        bob.setLastName(lastName);
        bob.setIsAdmin(false);
        bob.setIsGlobalAdmin(false);
        bob.setPassword("password");
        bob.setBio(bio);
        bob.setGender(gender);
        bob.setUserEmails(userEmails);
        bob.setPassports(passportCountries);
        bob.setActivityTypes(activityTypes);
        bob.setUsername(nickname);
        bob.setBirthDate(dob);
        bob.setFitnessLevel(fitness);
        bob.setActivityParticipator(new HashSet<>());
        bob.setActivityCreator(new HashSet<>());
        bob.setLocation(location);
        userRepository.save(bob);

        // Fred admin user initial setup
        User fredTheAdmin = new User();
        fredTheAdmin.setIsGlobalAdmin(true);
        userRepository.save(fredTheAdmin);

        bobToken = jwtTokenUtil.generateToken(bob);
        fredToken = jwtTokenUtil.generateToken(fredTheAdmin);
    }

    @Test
    void testRetrieveExistingUserShouldReturnFirstname() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/profiles/" + bob.getId())
                .cookie(new Cookie("token", bobToken)))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertEquals(firstName, JsonPath.parse(response).read("$.firstname"));
    }

    @Test
    void testRetrieveExistingUserShouldReturnMiddlename() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/profiles/" + bob.getId())
                .cookie(new Cookie("token", bobToken)))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertEquals(middleName, JsonPath.parse(response).read("$.middlename"));
    }

    @Test
    void testRetrieveExistingUserShouldReturnLastname() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/profiles/" + bob.getId())
                .cookie(new Cookie("token", bobToken)))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertEquals(lastName, JsonPath.parse(response).read("$.lastname"));
    }

    @Test
    void testRetrieveExistingUserShouldReturnNickname() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/profiles/" + bob.getId())
                .cookie(new Cookie("token", bobToken)))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertEquals(nickname, JsonPath.parse(response).read("$.nickname"));
    }

    @Test
    void testRetrieveExistingUserShouldReturnBio() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/profiles/" + bob.getId())
                .cookie(new Cookie("token", bobToken)))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertEquals(bio, JsonPath.parse(response).read("$.bio"));
    }

    @Test
    void testRetrieveExistingUserShouldReturnGender() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/profiles/" + bob.getId())
                .cookie(new Cookie("token", bobToken)))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertEquals(gender, JsonPath.parse(response).read("$.gender"));
    }

    @Test
    void testRetrieveExistingUserShouldReturnPrimaryEmail() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/profiles/" + bob.getId())
                .cookie(new Cookie("token", bobToken)))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertEquals(email, JsonPath.parse(response).read("$.primary_email.address"));
    }

    @Test
    void testRetrieveExistingUserShouldReturnSecondaryEmail() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/profiles/" + bob.getId())
                .cookie(new Cookie("token", bobToken)))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertEquals(emailSecondary, JsonPath.parse(response).read("$.additional_email[0].address"));
    }

    @Test
    void testRetrieveExistingUserShouldReturnDOB() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/profiles/" + bob.getId())
                .cookie(new Cookie("token", bobToken)))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertEquals(dob, JsonPath.parse(response).read("$.date_of_birth"));
    }

    @Test
    void testRetrieveExistingUserShouldReturnPassport() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/profiles/" + bob.getId())
                .cookie(new Cookie("token", bobToken)))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertEquals(passport, JsonPath.parse(response).read("$.passports[0].name"));
    }

    @Test
    void testRetrieveExistingUserShouldReturnFitness() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/profiles/" + bob.getId())
                .cookie(new Cookie("token", bobToken)))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertEquals(fitness, JsonPath.parse(response).read("$.fitness", Integer.class));
    }

    @Test
    void testRetrieveExistingUserShouldReturnLocation() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/profiles/" + bob.getId())
                .cookie(new Cookie("token", bobToken)))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertEquals(locationPlaceId, JsonPath.parse(response).read("$.location.placeId"));
    }

    @Test
    void testRetrieveNonExistingUserShouldReturn404() throws Exception {
        mockMvc.perform(get("/profiles/" + -1)
                .cookie(new Cookie("token", bobToken)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testRetrieveGlobalAdminUserShouldReturn404() throws Exception {
        mockMvc.perform(get("/profiles/" + -1)
                .cookie(new Cookie("token", fredToken)))
                .andExpect(status().isNotFound());
    }

}
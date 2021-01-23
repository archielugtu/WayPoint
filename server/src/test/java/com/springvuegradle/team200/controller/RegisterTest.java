package com.springvuegradle.team200.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.springvuegradle.team200.dto.request.RegisterRequest;
import com.springvuegradle.team200.model.ActivityType;
import com.springvuegradle.team200.model.User;
import com.springvuegradle.team200.model.UserEmail;
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
import org.springframework.test.web.servlet.MvcResult;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RegisterTest {

    private final List<String> passports = List.of("New Zealand");
    private final List<String> additionalEmails = List.of("jim@bob.com", "my@man.com");
    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

    private final List<String> activities = List.of("Quad Biking", "Badminton");
    private final List<String> activitiesInvalid = List.of("Studying", "Playing dota", "Eating");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    private RegisterRequest testRegister;

    private Long userId;

    @BeforeEach
    void setup() throws ParseException {
        userRepository.deleteAll();

        testRegister = new RegisterRequest();
        testRegister.setFirstName("Jerome");
        testRegister.setMiddleName("Francis");
        testRegister.setLastName("Grubb");
        testRegister.setUsername("Jdawg");
        testRegister.setPassword("bigChungus69");
        testRegister.setBio("just tryna get by");
        testRegister.setGender("Male");
        testRegister.setEmailAddress("jeromegrubb@gmail.com");
        testRegister.setFitnessLevel(4);
        testRegister.setBirthDate(formatter.parse("1985-12-20"));
        testRegister.setPassports(passports);
        testRegister.setActivities(activities);
        testRegister.setAdditionalEmails(additionalEmails);
    }

    @Test
    void testRegistrationOkShouldSaveFirstname() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/profiles")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(testRegister)))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        String id = JsonPath.parse(response).read("$.id");
        userId = Long.parseLong(id);
        User user = userService.read(userId);
        assertEquals("Jerome", user.getFirstName());
    }

    @Test
    void testRegistrationOkShouldSaveLastname() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/profiles")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(testRegister)))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        String id = JsonPath.parse(response).read("$.id");
        userId = Long.parseLong(id);
        User user = userService.read(userId);
        assertEquals("Grubb", user.getLastName());
    }

    @Test
    void testRegistrationOkShouldSaveMiddlename() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/profiles")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(testRegister)))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        String id = JsonPath.parse(response).read("$.id");
        userId = Long.parseLong(id);
        User user = userService.read(userId);
        assertEquals("Francis", user.getMiddleName());
    }

    @Test
    void testRegistrationOkShouldSaveNickname() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/profiles")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(testRegister)))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        String id = JsonPath.parse(response).read("$.id");
        userId = Long.parseLong(id);
        User user = userService.read(userId);
        assertEquals("Jdawg", user.getUsername());
    }

    @Test
    void testRegistrationOkShouldSavePassword() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/profiles")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(testRegister)))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        String id = JsonPath.parse(response).read("$.id");
        userId = Long.parseLong(id);
        User user = userService.read(userId);
        assertTrue(passwordEncoder.matches("bigChungus69", user.getPassword()));
    }

    @Test
    void testRegistrationOkShouldSaveBio() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/profiles")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(testRegister)))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        String id = JsonPath.parse(response).read("$.id");
        userId = Long.parseLong(id);
        User user = userService.read(userId);
        assertEquals("just tryna get by", user.getBio());
    }

    @Test
    void testRegistrationOkShouldSaveGender() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/profiles")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(testRegister)))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        String id = JsonPath.parse(response).read("$.id");
        userId = Long.parseLong(id);
        User user = userService.read(userId);
        assertEquals("Male", user.getGender());
    }

    @Test
    void testRegistrationOkShouldSavePrimaryEmail() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/profiles")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(testRegister)))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        String id = JsonPath.parse(response).read("$.id");
        userId = Long.parseLong(id);
        User user = userService.read(userId);
        assertEquals("jeromegrubb@gmail.com", user.getPrimaryEmail().getAddress());
    }

    @Test
    void testRegistrationOkShouldSaveFitnessLevel() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/profiles")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(testRegister)))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        String id = JsonPath.parse(response).read("$.id");
        userId = Long.parseLong(id);
        User user = userService.read(userId);
        assertEquals(4, user.getFitnessLevel());
    }

    @Test
    void testRegistrationOkShouldSaveBirthdate() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/profiles")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(testRegister)))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        String id = JsonPath.parse(response).read("$.id");
        userId = Long.parseLong(id);
        User user = userService.read(userId);
        assertEquals("1985-12-20", user.getBirthDate());
    }

    @Test
    void testRegistrationOkShouldSavePassports() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/profiles")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(testRegister)))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        String id = JsonPath.parse(response).read("$.id");
        userId = Long.parseLong(id);
        User user = userService.read(userId);
        assertEquals("New Zealand", user.getPassports().get(0).getName());
    }

    @Test
    void testRegistrationOkShouldSaveActivityTypes() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/profiles")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(testRegister)))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        String id = JsonPath.parse(response).read("$.id");
        userId = Long.parseLong(id);
        User user = userService.read(userId);
        List<String> activityTypes = user.getActivityTypes().stream().map(ActivityType::getType).collect(Collectors.toList());
        assertTrue(activityTypes.contains("Quad Biking"));
        assertTrue(activityTypes.contains("Badminton"));
    }

    @Test
    void testRegistrationOkShouldSaveSecondaryEmails() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/profiles")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(testRegister)))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        String id = JsonPath.parse(response).read("$.id");
        userId = Long.parseLong(id);
        User user = userService.read(userId);
        List<String> secondaryEmails = user.getUserEmails().stream().filter(e -> !e.getIsPrimary()).map(UserEmail::getAddress).collect(Collectors.toList());
        assertTrue(secondaryEmails.contains("jim@bob.com"));
        assertTrue(secondaryEmails.contains("my@man.com"));
    }

    @Test
    void testFirstNameNull() throws Exception {
        testRegister.setFirstName(null);
        mockMvc.perform(post("/profiles")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(testRegister)))
                .andExpect(status().isBadRequest());
        assertTrue(userRepository.findAll().isEmpty());
    }

    @Test
    void testFirstNameIsToolong() throws Exception {
        testRegister.setFirstName("MynameistoolongMynameistoolongMynameistoolongMyname");
        mockMvc.perform(post("/profiles")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(testRegister)))
                .andExpect(status().isBadRequest());
        assertTrue(userRepository.findAll().isEmpty());
    }

    @Test
    void testMiddleNameIsToolong() throws Exception {
        testRegister.setMiddleName("MynameistoolongMynameistoolongMynameistoolongMyname");
        mockMvc.perform(post("/profiles")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(testRegister)))
                .andExpect(status().isBadRequest());
        assertTrue(userRepository.findAll().isEmpty());
    }

    @Test
    void testLastNameNull() throws Exception {
        testRegister.setLastName(null);
        mockMvc.perform(post("/profiles")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(testRegister)))
                .andExpect(status().isBadRequest());
        assertTrue(userRepository.findAll().isEmpty());
    }

    @Test
    void testLastNameIsToolong() throws Exception {
        testRegister.setLastName("MynameistoolongMynameistoolongMynameistoolongMyname");
        mockMvc.perform(post("/profiles")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(testRegister)))
                .andExpect(status().isBadRequest());
        assertTrue(userRepository.findAll().isEmpty());
    }

    @Test
    void testNickNameIsToolong() throws Exception {
        testRegister.setUsername("MynameistoolongMynameistoolongMynameistoolongMyname");
        mockMvc.perform(post("/profiles")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(testRegister)))
                .andExpect(status().isBadRequest());
        assertTrue(userRepository.findAll().isEmpty());
    }

    @Test
    void testBioIsToolong() throws Exception {
        testRegister.setBio("MynameistoolongMynameistoolongMynameistoolongMynameMynameistoolongMynameistoolongMyn" +
                "ameistoolongMynameMynameistoolongMynameistoolongMynameistoolongMynameMynameistoolongMynameistoolon" +
                "gMynameistoolongMyname");
        mockMvc.perform(post("/profiles")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(testRegister)))
                .andExpect(status().isBadRequest());
        assertTrue(userRepository.findAll().isEmpty());
    }

    @Test
    void testPasswordNull() throws Exception {
        testRegister.setPassword(null);
        mockMvc.perform(post("/profiles")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(testRegister)))
                .andExpect(status().isBadRequest());
        assertTrue(userRepository.findAll().isEmpty());
    }

    @Test
    void testGenderNull() throws Exception {
        testRegister.setGender(null);
        MvcResult result = mockMvc.perform(post("/profiles")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(testRegister)))
                .andExpect(status().isBadRequest())
                .andReturn();
        assertTrue(userRepository.findAll().isEmpty());
    }

    @Test
    void testInvalidGender() throws Exception {
        testRegister.setGender("Just tryna get by");
        mockMvc.perform(post("/profiles")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(testRegister)))
                .andExpect(status().isBadRequest());
        assertTrue(userRepository.findAll().isEmpty());
    }

    @Test
    void testInvalidPassword() throws Exception {
        testRegister.setPassword("bigchung");
        mockMvc.perform(post("/profiles")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(testRegister)))
                .andExpect(status().isBadRequest());
        assertTrue(userRepository.findAll().isEmpty());
    }

    @Test
    void testInvalidFitnessLevelLower() throws Exception {
        testRegister.setFitnessLevel(-1);
        mockMvc.perform(post("/profiles")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(testRegister)))
                .andExpect(status().isBadRequest());
        assertTrue(userRepository.findAll().isEmpty());
    }

    @Test
    void testInvalidFitnessLevelHigher() throws Exception {
        testRegister.setFitnessLevel(5);
        mockMvc.perform(post("/profiles")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(testRegister)))
                .andExpect(status().isBadRequest());
        assertTrue(userRepository.findAll().isEmpty());
    }

    @Test
    void testBirthDateIsNull() throws Exception {
        testRegister.setBirthDate(null);
        mockMvc.perform(post("/profiles")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(testRegister)))
                .andExpect(status().isBadRequest());
        assertTrue(userRepository.findAll().isEmpty());
    }

    @Test
    void testInvalidBirthDateHigher() throws Exception {
        testRegister.setBirthDate(formatter.parse("1800-12-20"));
        mockMvc.perform(post("/profiles")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(testRegister)))
                .andExpect(status().isBadRequest());
        assertTrue(userRepository.findAll().isEmpty());
    }

    @Test
    void testInvalidBirthDateLower() throws Exception {
        testRegister.setBirthDate(formatter.parse("2015-12-20"));
        mockMvc.perform(post("/profiles")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(testRegister)))
                .andExpect(status().isBadRequest());
        assertTrue(userRepository.findAll().isEmpty());
    }

    @Test
    void testInvalidBirthDateFuture() throws Exception {
        testRegister.setBirthDate(formatter.parse("3000-12-20"));
        mockMvc.perform(post("/profiles")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(testRegister)))
                .andExpect(status().isBadRequest());
        assertTrue(userRepository.findAll().isEmpty());
    }

    @Test
    void testInvalidActivityType() throws Exception {
        testRegister.setActivities(activitiesInvalid);
        mockMvc.perform(post("/profiles")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(testRegister)))
                .andExpect(status().isBadRequest());
        assertTrue(userRepository.findAll().isEmpty());
    }

    @Test
    void testEmptyAdditionalEmailShouldBeAccepted() throws Exception {
        testRegister.setAdditionalEmails(new ArrayList<>());
        mockMvc.perform(post("/profiles")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(testRegister)))
                .andExpect(status().isOk());
        assertEquals(1, userRepository.findAll().size());
    }

    @Test
    void testInvalidAdditionalEmailShouldBeBadRequest() throws Exception {
        testRegister.setAdditionalEmails(new ArrayList<>(List.of("chungus")));
        mockMvc.perform(post("/profiles")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(testRegister)))
                .andExpect(status().isBadRequest());
        assertTrue(userRepository.findAll().isEmpty());
    }

    @Test
    void testMoreThanFourAdditionalEmailsShouldBeBadRequest() throws Exception {
        testRegister.setAdditionalEmails(new ArrayList<>(List.of("a@a.com", "b@b.com", "c@c.com", "d@d.com", "e@e.com")));
        mockMvc.perform(post("/profiles")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(testRegister)))
                .andExpect(status().isBadRequest());
        assertTrue(userRepository.findAll().isEmpty());
    }

    @Test
    void testRegisteringExistingPrimaryEmailShouldBeBadRequest() throws Exception {
        mockMvc.perform(post("/profiles")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(testRegister)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/profiles")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(testRegister)))
                .andExpect(status().isBadRequest());
        assertEquals(1, userRepository.findAll().size());
    }

    @Test
    void testRegisteringExistingSecondaryEmailShouldBeBadRequest() throws Exception {
        mockMvc.perform(post("/profiles")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(testRegister)))
                .andExpect(status().isOk());

        testRegister.setEmailAddress("nice@nice.com");
        testRegister.setAdditionalEmails(new ArrayList<>(List.of("jeromegrubb@gmail.com")));
        mockMvc.perform(post("/profiles")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(testRegister)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegisteringWithInvalidPassportShouldBeBadRequest() throws Exception {
        testRegister.setPassports(new ArrayList<>(List.of("Narnia")));
        mockMvc.perform(post("/profiles")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(testRegister)))
                .andExpect(status().isBadRequest());
        assertTrue(userRepository.findAll().isEmpty());
    }

    @Test
    void testRegisteringWithNoPassportShouldBeOk() throws Exception {
        testRegister.setPassports(new ArrayList<>());
        mockMvc.perform(post("/profiles")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(testRegister)))
                .andExpect(status().isOk());
        assertEquals(1, userRepository.findAll().size());
    }

    @Test
    void testRegisteringWithNoLocationShouldBeOk() throws Exception {
        testRegister.setLocation(null);
        mockMvc.perform(post("/profiles")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(testRegister)))
                .andExpect(status().isOk());
        assertEquals(1, userRepository.findAll().size());
    }
}

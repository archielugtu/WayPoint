package com.springvuegradle.team200.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springvuegradle.team200.dto.request.EditProfileRequest;
import com.springvuegradle.team200.jwt.JwtTokenUtil;
import com.springvuegradle.team200.model.Location;
import com.springvuegradle.team200.model.PassportCountry;
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

import javax.servlet.http.Cookie;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EditProfileTest {

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
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenUtil tokenUtil;



    @Autowired
    private PasswordEncoder passwordEncoder;

    private Long jackId;
    private Long wrongId;
    private String jackToken;
    private String jeromeToken;
    private String jackShortToken;
    private Long version;
    private EditProfileRequest editProfileRequest;
    private Location location;
    private Location newLocation;

    @BeforeEach
    void setup() throws ParseException {
        userRepository.deleteAll();

        User jack = new User("Jack", passwordEncoder.encode("password1"));
        User jerome = new User("Kevin", passwordEncoder.encode("kevinIsCool69"));

        userRepository.save(jack);
        version = jack.getVersion();
        jackId = jack.getId();
        userRepository.save(jerome);
        wrongId = jackId + 112345L;

        jackToken = tokenUtil.generateToken(jack);
        jackShortToken = tokenUtil.generateToken(jack, 1L);
        jeromeToken = tokenUtil.generateToken(jerome);

        location = new Location();
        location.setPlaceId("placeId");

        newLocation = new Location();
        newLocation.setPlaceId("newPlaceId");

        editProfileRequest = new EditProfileRequest();
        editProfileRequest.setFirstName("newfirstname");
        editProfileRequest.setLastName("newlastname");
        editProfileRequest.setMiddleName("newmiddlename");
        editProfileRequest.setNickname("newnickname");
        editProfileRequest.setBio("newbio");
        editProfileRequest.setGender("female");
        editProfileRequest.setFitnessLevel(2);
        editProfileRequest.setPrimaryEmail("jack@gmail.com");
        editProfileRequest.setAdditionalEmails(jackEmailStrings);
        editProfileRequest.setBirthDate(formatter.parse("2001-10-20"));
        editProfileRequest.setPassports(jackPassports);
        editProfileRequest.setLocation(newLocation);
    }

    @Test
    void testWithCorrectAllInputShouldUpdateFirstname() throws Exception {
        User user = userRepository.findById(jackId).get();
        assertNotEquals("newfirstname", user.getFirstName());
        mockMvc.perform(put("/profiles/" + jackId)
                .cookie(new Cookie("token", jackToken))
                .header("if-match", version)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(editProfileRequest)))
                .andExpect(status().isOk());
        user = userRepository.findById(jackId).get();
        assertEquals("newfirstname", user.getFirstName());
    }

    @Test
    void testWithCorrectAllInputShouldUpdateMiddlename() throws Exception {
        User user = userRepository.findById(jackId).get();
        assertNotEquals("newmiddlename", user.getMiddleName());
        mockMvc.perform(put("/profiles/" + jackId)
                .cookie(new Cookie("token", jackToken))
                .header("if-match", version)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(editProfileRequest)))
                .andExpect(status().isOk());
        user = userRepository.findById(jackId).get();
        assertEquals("newmiddlename", user.getMiddleName());
    }

    @Test
    void testWithCorrectAllInputShouldUpdateLastname() throws Exception {
        User user = userRepository.findById(jackId).get();
        assertNotEquals("newlastname", user.getLastName());
        mockMvc.perform(put("/profiles/" + jackId)
                .cookie(new Cookie("token", jackToken))
                .header("if-match", version)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(editProfileRequest)))
                .andExpect(status().isOk());
        user = userRepository.findById(jackId).get();
        assertEquals("newlastname", user.getLastName());
    }

    @Test
    void testWithCorrectAllInputShouldUpdateNickname() throws Exception {
        User user = userRepository.findById(jackId).get();
        assertNotEquals("newnickname", user.getUsername());
        mockMvc.perform(put("/profiles/" + jackId)
                .cookie(new Cookie("token", jackToken))
                .header("if-match", version)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(editProfileRequest)))
                .andExpect(status().isOk());
        user = userRepository.findById(jackId).get();
        assertEquals("newnickname", user.getUsername());
    }

    @Test
    void testWithCorrectAllInputShouldUpdateBio() throws Exception {
        User user = userRepository.findById(jackId).get();
        assertNotEquals("newbio", user.getBio());
        mockMvc.perform(put("/profiles/" + jackId)
                .cookie(new Cookie("token", jackToken))
                .header("if-match", version)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(editProfileRequest)))
                .andExpect(status().isOk());
        user = userRepository.findById(jackId).get();
        assertEquals("newbio", user.getBio());
    }

    @Test
    void testWithCorrectAllInputShouldUpdateGender() throws Exception {
        User user = userRepository.findById(jackId).get();
        assertNotEquals("female", user.getGender());
        mockMvc.perform(put("/profiles/" + jackId)
                .cookie(new Cookie("token", jackToken))
                .header("if-match", version)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(editProfileRequest)))
                .andExpect(status().isOk());
        user = userRepository.findById(jackId).get();
        assertEquals("female", user.getGender());
    }

    @Test
    void testWithCorrectAllInputShouldUpdateFitness() throws Exception {
        User user = userRepository.findById(jackId).get();
        assertNotEquals(2, user.getFitnessLevel());
        mockMvc.perform(put("/profiles/" + jackId)
                .cookie(new Cookie("token", jackToken))
                .header("if-match", version)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(editProfileRequest)))
                .andExpect(status().isOk());
        user = userRepository.findById(jackId).get();
        assertEquals(2, user.getFitnessLevel());
    }

    @Test
    void testWithCorrectAllInputShouldUpdatePassports() throws Exception {
        User user = userService.read(jackId);
        List<String> passports = user.getPassports().stream()
                .map(PassportCountry::getName)
                .collect(Collectors.toList());
        assertFalse(passports.contains("Afghanistan"));
        assertFalse(passports.contains("Sudan"));
        mockMvc.perform(put("/profiles/" + jackId)
                .cookie(new Cookie("token", jackToken))
                .header("if-match", version)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(editProfileRequest)))
                .andExpect(status().isOk());
        user = userService.read(jackId);
        passports = user.getPassports().stream()
                .map(PassportCountry::getName)
                .collect(Collectors.toList());
        assertTrue(passports.contains("Afghanistan"));
        assertTrue(passports.contains("Sudan"));
    }

    @Test
    void testWithCorrectAllInputShouldUpdateBirthdate() throws Exception {
        User user = userService.read(jackId);
        assertNotEquals("2001-10-20", user.getBirthDate());
        mockMvc.perform(put("/profiles/" + jackId)
                .cookie(new Cookie("token", jackToken))
                .header("if-match", version)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(editProfileRequest)))
                .andExpect(status().isOk());
        user = userService.read(jackId);
        assertEquals("2001-10-20", user.getBirthDate());
    }

    @Test
    void testWithCorrectAllInputShouldUpdateLocation() throws Exception {
        User user = userService.read(jackId);
        assertNull(user.getLocation());
        mockMvc.perform(put("/profiles/" + jackId)
                .cookie(new Cookie("token", jackToken))
                .header("if-match", version)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(editProfileRequest)))
                .andExpect(status().isOk());
        user = userService.read(jackId);
        assertEquals(newLocation.getPlaceId(), user.getLocation().getPlaceId());
    }

    @Test
    void testWithCorrectAllInputShouldUpdatePrimaryEmail() throws Exception {
        User user = userService.read(jackId);
        assertNotEquals("jack@gmail.com", user.getPrimaryEmail().getAddress());
        mockMvc.perform(put("/profiles/" + jackId)
                .cookie(new Cookie("token", jackToken))
                .header("if-match", version)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(editProfileRequest)))
                .andExpect(status().isOk());
        user = userService.read(jackId);
        assertEquals("jack@gmail.com", user.getPrimaryEmail().getAddress());
    }

    @Test
    void testWithCorrectAllInputShouldUpdateAdditionalEmails() throws Exception {
        User user = userService.read(jackId);
        List<String> additionalEmails = user.getUserEmails()
                .stream()
                .filter(e -> !e.getIsPrimary())
                .map(UserEmail::getAddress)
                .collect(Collectors.toList());
        assertFalse(additionalEmails.contains("Jack@yeet.com"));
        assertFalse(additionalEmails.contains("Jack@swag.com"));
        assertFalse(additionalEmails.contains("Jack@cool.com"));
        mockMvc.perform(put("/profiles/" + jackId)
                .cookie(new Cookie("token", jackToken))
                .header("if-match", version)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(editProfileRequest)))
                .andExpect(status().isOk());
        user = userService.read(jackId);
        additionalEmails = user.getUserEmails()
                .stream()
                .filter(e -> !e.getIsPrimary())
                .map(UserEmail::getAddress)
                .collect(Collectors.toList());
        assertTrue(additionalEmails.contains("Jack@yeet.com"));
        assertTrue(additionalEmails.contains("Jack@swag.com"));
        assertTrue(additionalEmails.contains("Jack@cool.com"));
    }

    @Test
    void testCorrectInputThatsRequired() throws Exception {
        EditProfileRequest mandatoryOnly = new EditProfileRequest();
        mandatoryOnly.setFirstName("newfirstname");
        mandatoryOnly.setLastName("newlastname");
        mandatoryOnly.setMiddleName(null);
        mandatoryOnly.setNickname(null);
        mandatoryOnly.setBio("newbio");
        mandatoryOnly.setGender("male");
        mandatoryOnly.setFitnessLevel(2);
        mandatoryOnly.setPrimaryEmail("jack@gmail.com");
        mandatoryOnly.setAdditionalEmails(jackEmailStrings);
        mandatoryOnly.setBirthDate(formatter.parse("2001-10-20"));
        mandatoryOnly.setPassports(jackPassports);

        mockMvc.perform(put("/profiles/" + jackId)
                .cookie(new Cookie("token", jackToken))
                .header("if-match", version)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(mandatoryOnly)))
                .andExpect(status().isOk());
    }

    @Test
    void testFailWhenFirstNameIsEmpty() throws Exception {
        editProfileRequest.setFirstName("");
        mockMvc.perform(put("/profiles/" + jackId)
                .cookie(new Cookie("token", jackToken))
                .header("if-match", version)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(editProfileRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testFailWhenFirstNameIsTooLong() throws Exception {
        editProfileRequest.setFirstName("MynameistoolongMynameistoolongMynameistoolongMyname");
        mockMvc.perform(put("/profiles/" + jackId)
                .cookie(new Cookie("token", jackToken))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(editProfileRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testFailWhenMiddleNameIsTooLong() throws Exception {
        editProfileRequest.setMiddleName("MynameistoolongMynameistoolongMynameistoolongMyname");
        mockMvc.perform(put("/profiles/" + jackId)
                .cookie(new Cookie("token", jackToken))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(editProfileRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testFailWhenLastNameIsEmpty() throws Exception {
        editProfileRequest.setLastName("");
        mockMvc.perform(put("/profiles/" + jackId)
                .cookie(new Cookie("token", jackToken))
                .header("if-match", version)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(editProfileRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testFailWhenLastNameIsTooLong() throws Exception {
        editProfileRequest.setLastName("MynameistoolongMynameistoolongMynameistoolongMyname");
        mockMvc.perform(put("/profiles/" + jackId)
                .cookie(new Cookie("token", jackToken))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(editProfileRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testFailWhenNickNameIsTooLong() throws Exception {
        editProfileRequest.setNickname("MynameistoolongMynameistoolongMynameistoolongMyname");
        mockMvc.perform(put("/profiles/" + jackId)
                .cookie(new Cookie("token", jackToken))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(editProfileRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testFailWhenbioIsTooLong() throws Exception {
        editProfileRequest.setBio("MynameistoolongMynameistoolongMynameistoolongMynameMynameistoolongMynameistoolongMyn" +
                "ameistoolongMynameMynameistoolongMynameistoolongMynameistoolongMynameMynameistoolongMynameistoolon" +
                "gMynameistoolongMyname");
        mockMvc.perform(put("/profiles/" + jackId)
                .cookie(new Cookie("token", jackToken))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(editProfileRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testFailWhenGenderIsEmpty() throws Exception {
        editProfileRequest.setGender("");
        mockMvc.perform(put("/profiles/" + jackId)
                .header("if-match", version)
                .cookie(new Cookie("token", jackToken))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(editProfileRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testFailWhenGenderIsOtherOption() throws Exception {
        editProfileRequest.setGender("Martians");
        mockMvc.perform(put("/profiles/" + jackId)
                .cookie(new Cookie("token", jackToken))
                .header("if-match", version)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(editProfileRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testFailWhenBirthdayEmpty() throws Exception {
        editProfileRequest.setBirthDate(null);
        mockMvc.perform(put("/profiles/" + jackId)
                .cookie(new Cookie("token", jackToken))
                .header("if-match", version)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(editProfileRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testFailWhenBirthdayInfuture() throws Exception {
        editProfileRequest.setBirthDate(formatter.parse("3020-01-01"));
        mockMvc.perform(put("/profiles/" + jackId)
                .cookie(new Cookie("token", jackToken))
                .header("if-match", version)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(editProfileRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testFailWhenBirthdayTooLow() throws Exception {
        editProfileRequest.setBirthDate(formatter.parse("2019-01-01"));
        mockMvc.perform(put("/profiles/" + jackId)
                .cookie(new Cookie("token", jackToken))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(editProfileRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testFailWhenBirthdayTooHigh() throws Exception {
        editProfileRequest.setBirthDate(formatter.parse("1800-01-01"));
        mockMvc.perform(put("/profiles/" + jackId)
                .cookie(new Cookie("token", jackToken))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(editProfileRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testFailLongEmail() throws Exception {
        editProfileRequest.setPrimaryEmail("primary@email.com");
        editProfileRequest.setAdditionalEmails(longListOfEmailStrings);
        mockMvc.perform(put("/profiles/" + jackId)
                .cookie(new Cookie("token", jackToken))
                .header("if-match", version)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(editProfileRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testWrongToken() throws Exception {
        mockMvc.perform(put("/profiles/" + jackId)
                .cookie(new Cookie("token", jeromeToken))
                .header("if-match", version)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(editProfileRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testUnauthorisedUser() throws Exception {
        mockMvc.perform(put("/profiles/" + jackId)
                .cookie(new Cookie("token", jackShortToken))
                .header("if-match", version)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(editProfileRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testWrongId() throws Exception {
        mockMvc.perform(put("/profiles/" + wrongId)
                .cookie(new Cookie("token", jackToken))
                .header("if-match", version)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(editProfileRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testUpdateConcurrentlyShouldFail() throws Exception {
        // Update a user as usual
        mockMvc.perform(put("/profiles/" + jackId)
                .cookie(new Cookie("token", jackToken))
                .header("if-match", version)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(editProfileRequest)))
                .andExpect(status().isOk());

        // Specifying an old version in if-match header should fail
        mockMvc.perform(put("/profiles/" + jackId)
                .cookie(new Cookie("token", jackToken))
                .header("if-match", version)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(editProfileRequest)))
                .andExpect(status().isPreconditionFailed());
    }

    @Test
    void testUpdateNotConcurrentlyShouldSuccess() throws Exception {
        // Update a user as usual
        mockMvc.perform(put("/profiles/" + jackId)
                .cookie(new Cookie("token", jackToken))
                .header("if-match", version)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(editProfileRequest)))
                .andExpect(status().isOk());

        User j = userRepository.getUserFromId(jackId);

        // We can only update when we specify the correct version in if-match header
        mockMvc.perform(put("/profiles/" + jackId)
                .cookie(new Cookie("token", jackToken))
                .header("if-match", j.getVersion())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(editProfileRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateInvalidIfMatchHeaderShouldFail() throws Exception {
        mockMvc.perform(put("/profiles/" + jackId)
                .cookie(new Cookie("token", jackToken))
                .header("if-match", -1)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(editProfileRequest)))
                .andExpect(status().isPreconditionFailed());
    }

    @Test
    void testUpdateWithoutIfMatchHeaderShouldFail() throws Exception {
        mockMvc.perform(put("/profiles/" + jackId)
                .cookie(new Cookie("token", jackToken))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(editProfileRequest)))
                .andExpect(status().isBadRequest());

        mockMvc.perform(put("/profiles/" + jackId)
                .cookie(new Cookie("token", jackToken))
                .header("if-match", "")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(editProfileRequest)))
                .andExpect(status().isBadRequest());
    }
}

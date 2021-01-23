package com.springvuegradle.team200.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springvuegradle.team200.dto.request.PasswordRequest;
import com.springvuegradle.team200.jwt.JwtTokenUtil;
import com.springvuegradle.team200.model.User;
import com.springvuegradle.team200.repository.UserRepository;
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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EditPasswordTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    private User jack;
    private User jerome;
    private Long jackId;
    private Long wrongId;
    private String jackToken;
    private String jeromeToken;
    private String jackShortToken;

    private PasswordRequest testPassword = new PasswordRequest("password1", "password2", "password2");
    private PasswordRequest badPasswordRequest = new PasswordRequest("wrongPass", "password2", "password2");
    private PasswordRequest confirmPasswordWrong = new PasswordRequest("password1", "password2", "password3");

    @BeforeEach
    void setup() {
        userRepository.deleteAll();

        jack = new User("Jack", passwordEncoder.encode("password1"));
        jerome = new User("Jerome", passwordEncoder.encode("password10"));

        userRepository.save(jack);
        jackId = jack.getId();
        userRepository.save(jerome);
        wrongId = jackId + 1L;

        jackToken = jwtTokenUtil.generateToken(jack);
        jackShortToken = jwtTokenUtil.generateToken(jack, 1L);
        jeromeToken = jwtTokenUtil.generateToken(jerome);
    }

    @Test
    void testPassing() throws Exception {
        mockMvc.perform(post("/profiles/" + jackId + "/password")
                .cookie(new Cookie("token", jackToken))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(testPassword)))
                .andExpect(status().isOk());

        jack = userRepository.findById(jackId).get();
        assertTrue(passwordEncoder.matches("password2", jack.getPassword()));

    }

    @Test
    void testWrongPassword() throws Exception {
        mockMvc.perform(post("/profiles/" + jackId + "/password")
                .cookie(new Cookie("token", jackToken))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(badPasswordRequest)))
                .andExpect(status().isBadRequest());

        assertTrue(passwordEncoder.matches("password1", jack.getPassword()));
    }

    @Test
    void testWrongRepeatPassword() throws Exception {
        mockMvc.perform(post("/profiles/" + jackId + "/password")
                .cookie(new Cookie("token", jackToken))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(confirmPasswordWrong)))
                .andExpect(status().isBadRequest());
        assertTrue(passwordEncoder.matches("password1", jack.getPassword()));
    }

    @Test
    void testWrongToken() throws Exception {
        mockMvc.perform(post("/profiles/" + jackId + "/password")
                .cookie(new Cookie("token", jeromeToken))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(testPassword)))
                .andExpect(status().isForbidden());
        assertTrue(passwordEncoder.matches("password1", jack.getPassword()));
    }

    @Test
    void testUnauthorisedUser() throws Exception {
        mockMvc.perform(post("/profiles/" + jackId + "/password")
                .cookie(new Cookie("token", jackShortToken))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(testPassword)))
                .andExpect(status().isUnauthorized());
        assertTrue(passwordEncoder.matches("password1", jack.getPassword()));
    }

    @Test
    void testWrongId() throws Exception {
        mockMvc.perform(post("/profiles/" + wrongId + "/password")
                .cookie(new Cookie("token", jackToken))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(testPassword)))
                .andExpect(status().isForbidden());
        assertTrue(passwordEncoder.matches("password1", jack.getPassword()));
    }

    @Test
    void testChangePasswordByAdminShouldNotRequireOldPassword() throws Exception {
        assertTrue(passwordEncoder.matches("password1", jack.getPassword()));

        jerome.setIsAdmin(true);
        userRepository.save(jerome);

        testPassword.setPassword("");

        mockMvc.perform(post("/profiles/" + jackId + "/password")
                .cookie(new Cookie("token", jeromeToken))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(testPassword)))
                .andExpect(status().isOk());

        jack = userRepository.findById(jackId).get();
        assertTrue(passwordEncoder.matches("password2", jack.getPassword()));
    }
}

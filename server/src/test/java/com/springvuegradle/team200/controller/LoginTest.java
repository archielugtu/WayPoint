package com.springvuegradle.team200.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.springvuegradle.team200.dto.request.LoginRequest;
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
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LoginTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User jack;

    private LoginRequest passLoginRequest = new LoginRequest("Jack", "password1");
    private LoginRequest failLoginRequest = new LoginRequest("Jack", "password69");
    private LoginRequest notFoundLoginRequest = new LoginRequest("Kevin", "password1");
    private LoginRequest emptyUsernameLoginRequest = new LoginRequest("", "password1");
    private LoginRequest emptyPasswordLoginRequest = new LoginRequest("Jack", "");
    private LoginRequest abnormalPasswordLoginRequest = new LoginRequest("Jack", "\uD83D\uDE00"); //smiley emoji

    @BeforeEach
    void setup() {
        userRepository.deleteAll();

        jack = new User("Jack", passwordEncoder.encode("password1"));
        userRepository.save(jack);
    }

    @Test
    void testPassingLogin() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/login")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(passLoginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("status").value("SUCCESS"))
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertEquals(jack.getId(), JsonPath.parse(response).read("$.userId", Long.class));
    }

    @Test
    void testPassingLoginAdmin() throws Exception {
        jack.setIsAdmin(true);
        userRepository.save(jack);
        MvcResult mvcResult = mockMvc.perform(post("/login")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(passLoginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("status").value("SUCCESS"))
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertEquals(jack.getId(), JsonPath.parse(response).read("$.userId", Long.class));
        assertEquals(jack.getIsAdmin(), JsonPath.parse(response).read("$.isAdmin", Boolean.class));
        assertEquals(jack.getIsGlobalAdmin(), JsonPath.parse(response).read("$.isGlobalAdmin", Boolean.class));
    }


    @Test
    void testPassingLoginGlobalAdmin() throws Exception {
        jack.setIsGlobalAdmin(true);
        userRepository.save(jack);
        MvcResult mvcResult = mockMvc.perform(post("/login")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(passLoginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("status").value("SUCCESS"))
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertEquals(jack.getId(), JsonPath.parse(response).read("$.userId", Long.class));
        assertEquals(jack.getIsAdmin(), JsonPath.parse(response).read("$.isAdmin", Boolean.class));
        assertEquals(jack.getIsGlobalAdmin(), JsonPath.parse(response).read("$.isGlobalAdmin", Boolean.class));

    }
    @Test
    void testWrongPasswordLogin() throws Exception {
        mockMvc.perform(post("/login")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(failLoginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value("WRONG PASSWORD"));
    }

    @Test
    void testNotFoundLogin() throws Exception {
        mockMvc.perform(post("/login")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(notFoundLoginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value("USER NOT FOUND"));
    }

    @Test
    void testEmptyUsernameLoginShouldFail() throws Exception {
        mockMvc.perform(post("/login")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(emptyUsernameLoginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value("USER NOT FOUND"));
    }

    @Test
    void testEmptyPasswordLoginShouldFail() throws Exception {
        mockMvc.perform(post("/login")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(emptyPasswordLoginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value("WRONG PASSWORD"));
    }

    @Test
    void testLoggingInWithAbnormalPasswordShouldFail() throws Exception {
        mockMvc.perform(post("/login")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(abnormalPasswordLoginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value("WRONG PASSWORD"));
    }
}

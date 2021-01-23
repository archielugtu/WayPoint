package com.springvuegradle.team200.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.springvuegradle.team200.dto.request.EditEmailRequest;
import com.springvuegradle.team200.jwt.JwtTokenUtil;
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

import javax.servlet.http.Cookie;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserEmailTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    private List<String> jackEmailStrings = List.of("Jack@swag.com", "Jack@cool.com", "Jack@yeet.com");
    private List<String> longListOfEmailStrings = List.of("abc@yeet.com", "abc@yeet2.com",
            "abc@yeet3.com", "abc@yeet4.com", "abc@yeet5.com", "abc@yeet6.com");
    private Long jackId;
    private User jack;
    private String jackToken;
    private String kevinToken;

    private EditEmailRequest existingEmailRequest = new EditEmailRequest("Jacknew@yeet.com", jackEmailStrings);
    private EditEmailRequest kevinsEmailRequest = new EditEmailRequest("kevin@cool.com", new ArrayList<>());
    private EditEmailRequest longListEmailRequest = new EditEmailRequest("yeet@cool.com", longListOfEmailStrings);


    @BeforeEach
    void setup() {
        userRepository.deleteAll();

        jack = new User("Jack", passwordEncoder.encode("password1"));
        User kevin = new User("Kevin", passwordEncoder.encode("kevinIsCool69"));

        UserEmail jackEmail1 = new UserEmail("Jack@swag.com", jack, true);
        UserEmail jackEmail2 = new UserEmail("Jack@cool.com", jack, false);
        UserEmail jackEmail3 = new UserEmail("Jack@yeet.com", jack, false);

        UserEmail kevinEmail = new UserEmail("kevin@cool.com", kevin, true);

        jack.setUserEmails(List.of(jackEmail1, jackEmail2, jackEmail3));
        kevin.setUserEmails(List.of(kevinEmail));

        userRepository.save(jack);
        userRepository.save(kevin);
        jackId = jack.getId();
        jackToken = jwtTokenUtil.generateToken(jack);
        kevinToken = jwtTokenUtil.generateToken(kevin);
    }

    @Test
    void testRequestWithMyExistingEmails() throws Exception {
        jack = userService.read(jackId);
        assertEquals("Jack@swag.com", jack.getPrimaryEmail().getAddress());

        List<String> additionalEmails = jack.getUserEmails().stream()
                .filter(e -> !e.getIsPrimary())
                .map(UserEmail::getAddress)
                .collect(Collectors.toList());
        assertFalse(additionalEmails.contains("Jack@swag.com"));
        assertTrue(additionalEmails.contains("Jack@cool.com"));
        assertTrue(additionalEmails.contains("Jack@yeet.com"));

        mockMvc.perform(put("/profiles/" + jackId + "/emails")
                .cookie(new Cookie("token", jackToken))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(existingEmailRequest)))
                .andExpect(status().isOk());

        jack = userService.read(jackId);
        assertEquals("Jacknew@yeet.com", jack.getPrimaryEmail().getAddress());

        additionalEmails = jack.getUserEmails().stream()
                .filter(e -> !e.getIsPrimary())
                .map(UserEmail::getAddress)
                .collect(Collectors.toList());
        assertTrue(additionalEmails.contains("Jack@swag.com"));
        assertTrue(additionalEmails.contains("Jack@cool.com"));
        assertTrue(additionalEmails.contains("Jack@yeet.com"));
    }

    @Test
    void testChangeEmailWithWrongToken() throws Exception {
        mockMvc.perform(put("/profiles/" + jackId + "/emails")
                .cookie(new Cookie("token", kevinToken))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(existingEmailRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testChangeEmailWithoutToken() throws Exception {
        mockMvc.perform(put("/profiles/" + jackId + "/emails")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(existingEmailRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testRequestWithExistingEmailsThatBelongToAnotherUser() throws Exception {
        mockMvc.perform(put("/profiles/" + jackId + "/emails")
                .cookie(new Cookie("token", jackToken))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(kevinsEmailRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRequestWithTooManyEmails() throws Exception {
        mockMvc.perform(put("/profiles/" + jackId + "/emails")
                .cookie(new Cookie("token", jackToken))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(longListEmailRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRequestWithRetrieveAllEmails() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/profiles/emails")
                .cookie(new Cookie("token", jackToken)))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertEquals("Jack@cool.com", JsonPath.parse(response).read("$[0]"));
        assertEquals("kevin@cool.com", JsonPath.parse(response).read("$[3]"));
    }
}

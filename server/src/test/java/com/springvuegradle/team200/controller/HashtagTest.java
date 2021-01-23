package com.springvuegradle.team200.controller;

import com.springvuegradle.team200.jwt.JwtTokenUtil;
import com.springvuegradle.team200.model.Hashtag;
import com.springvuegradle.team200.model.User;
import com.springvuegradle.team200.repository.ActivityRepository;
import com.springvuegradle.team200.repository.HashtagRepository;
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
import java.util.List;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class HashtagTest {

    @Autowired
    private HashtagRepository hashtagRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ActivityRepository activityRepository;

    private User bobby;

    @BeforeEach
    void setup() {
        activityRepository.deleteAll();
        hashtagRepository.deleteAll();
        userRepository.deleteAll();
        Set<Hashtag> startingHashtags = Set.of(new Hashtag("#yes"), new Hashtag("#no"), new Hashtag("#maybe"), new Hashtag("#notsure"), new Hashtag("#youdecide"));
        hashtagRepository.saveAll(startingHashtags);
        bobby = new User("Bobby", passwordEncoder.encode("passwnnord1"));
        userRepository.saveAll(List.of(bobby));
    }

    @Test
    void testGetReturnsAllHashtags() throws Exception {
        mockMvc.perform(get("/activities/hashtags")
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(bobby))))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void testGetHashTagsWhenEmpty() throws Exception {
        hashtagRepository.deleteAll();
        mockMvc.perform(get("/activities/hashtags")
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(bobby))))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void testGetHashTagsWithNoAuth() throws Exception {
        hashtagRepository.deleteAll();
        mockMvc.perform(get("/activities/hashtags"))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }
}

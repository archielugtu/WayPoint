package com.springvuegradle.team200.controller;

import com.jayway.jsonpath.JsonPath;
import com.springvuegradle.team200.AbstractInitialiser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MvcResult;

import javax.servlet.http.Cookie;
import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ActivityRoleTest extends AbstractInitialiser {

    public ActivityRoleTest() throws ParseException {
        super();
    }

    @Test
    void testCreatorIsCreator() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/activities/" + marathon.getId() + "/role/" + creatorUser.getId())
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(admin))))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertEquals("creator", JsonPath.parse(response).read("$.role"));
    }

    @Test
    void testOrganiserIsOrganiser() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/activities/" + marathon.getId() + "/role/" + organiserUser.getId())
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(admin))))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertEquals("organiser", JsonPath.parse(response).read("$.role"));
    }

    @Test
    void testParticipantIsParticipant() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/activities/" + marathon.getId() + "/role/" + participantUser.getId())
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(admin))))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertEquals("participant", JsonPath.parse(response).read("$.role"));
    }

    @Test
    void testFollowerIsFollower() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/activities/" + marathon.getId() + "/role/" + followerUser.getId())
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(admin))))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertEquals("follower", JsonPath.parse(response).read("$.role"));
    }

    @Test
    void testNoneisNone() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/activities/" + marathon.getId() + "/role/" + noneUser.getId())
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(admin))))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertEquals("none", JsonPath.parse(response).read("$.role"));
    }
}

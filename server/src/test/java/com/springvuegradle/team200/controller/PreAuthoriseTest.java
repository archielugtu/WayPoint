package com.springvuegradle.team200.controller;

import com.springvuegradle.team200.AbstractInitialiser;
import com.springvuegradle.team200.model.Activity;
import com.springvuegradle.team200.model.ActivityVisibility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.servlet.http.Cookie;
import java.text.ParseException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PreAuthoriseTest extends AbstractInitialiser {

    Activity activity = marathon;

    public PreAuthoriseTest() throws ParseException {
        super();
    }

    @Test
    void testEditRequestWithCreator() throws Exception {
        mockMvc.perform(get("/Debug/creatorAndOrganiser/activities/" + marathon.getId())
                .cookie(new Cookie("token", creatorToken))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(activityEditRequest)))
                .andExpect(status().isOk());
    }
    @Test
    void testEditRequestInvalid() throws Exception {
        mockMvc.perform(get("/Debug/creatorAndOrganiser/activities/" + marathon.getId())
                .cookie(new Cookie("token", participatorToken))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(activityEditRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testEditRequestWithOrganiser() throws Exception {
        mockMvc.perform(get("/Debug/creatorAndOrganiser/activities/" + marathon.getId())
                .cookie(new Cookie("token", organiserToken))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(activityEditRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void testEditRequestWithAdmin() throws Exception {
        mockMvc.perform(get("/Debug/creatorAndOrganiser/activities/" + marathon.getId())
                .cookie(new Cookie("token", adminToken))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(activityEditRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void testGetPrivateActivityWithCreator() throws Exception {
        marathon.setVisibility(ActivityVisibility.PRIVATE);
        activityRepository.save(marathon);
        mockMvc.perform(get("/Debug/creator/activities/" + marathon.getId())
                .cookie(new Cookie("token", creatorToken)))
                .andExpect(status().isOk());
    }

    @Test
    void testGetPrivateActivityWithOtherUser() throws Exception {
        marathon.setVisibility(ActivityVisibility.PRIVATE);
        activityRepository.save(marathon);
        mockMvc.perform(get("/Debug/creator/activities/" + marathon.getId())
                .cookie(new Cookie("token", organiserToken)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetRestrictedWithOrganiserRole() throws Exception {
        marathon.setVisibility(ActivityVisibility.RESTRICTED);
        activityRepository.save(marathon);
        marathon.setVisibility(ActivityVisibility.RESTRICTED);
        mockMvc.perform(get("/Debug/creator/activities/" + marathon.getId())
                .cookie(new Cookie("token", organiserToken)))
                .andExpect(status().isOk());
    }

    @Test
    void testGetRestrictedWithNoneRole() throws Exception {
        marathon.setVisibility(ActivityVisibility.RESTRICTED);
        activityRepository.save(marathon);
        marathon.setVisibility(ActivityVisibility.RESTRICTED);
        mockMvc.perform(get("/Debug/creator/activities/" + marathon.getId())
                .cookie(new Cookie("token", noneToken)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetPublicWithNoneRole() throws Exception {
        marathon.setVisibility(ActivityVisibility.PUBLIC);
        mockMvc.perform(get("/Debug/creator/activities/" + marathon.getId())
                .cookie(new Cookie("token", noneToken)))
                .andExpect(status().isOk());
    }

    @Test
    void testGetPrivateWithAdmin() throws Exception {
        marathon.setVisibility(ActivityVisibility.PRIVATE);
        activityRepository.save(marathon);
        marathon.setVisibility(ActivityVisibility.PRIVATE);
        mockMvc.perform(get("/Debug/creator/activities/" + marathon.getId())
                .cookie(new Cookie("token", adminToken)))
                .andExpect(status().isOk());
    }

}

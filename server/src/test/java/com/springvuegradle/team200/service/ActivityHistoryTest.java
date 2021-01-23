package com.springvuegradle.team200.service;

import com.springvuegradle.team200.AbstractInitialiser;
import com.springvuegradle.team200.dto.request.EditActivityRoleRequest;
import com.springvuegradle.team200.dto.response.SingleHistoryResponse;
import com.springvuegradle.team200.model.ActivityRole;
import com.springvuegradle.team200.model.UserActivityHistory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.servlet.http.Cookie;
import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ActivityHistoryTest extends AbstractInitialiser {

    private final int PAGE = 0;
    private final int SIZE = 10;

    public ActivityHistoryTest() throws ParseException {
        super();
    }

    @Test
    void testUpdateActivityShouldSaveHistory() throws Exception {
        mockMvc.perform(put("/profiles/" + creatorUser.getId() + "/activities/" + marathon.getId())
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(creatorUser)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(activityEditRequest)))
                .andExpect(status().isOk());

        assertEquals(1, userActivityHistoryRepository.findAll().size());
    }

    @Test
    void testUpdateActivityShouldSaveDate() throws Exception {
        mockMvc.perform(put("/profiles/" + creatorUser.getId() + "/activities/" + marathon.getId())
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(creatorUser)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(activityEditRequest)))
                .andExpect(status().isOk());

        assertEquals(1, userActivityHistoryRepository.findAll().size());
        UserActivityHistory history = userActivityHistoryRepository.findAll().get(0);
        assertNotNull(history.getTimestamp());
    }

    @Test
    void testDeleteActivityShouldDeleteHistories() throws Exception {
        mockMvc.perform(delete("/profiles/" + creatorUser.getId() + "/activities/" + marathon.getId())
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(creatorUser)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(activityEditRequest)))
                .andExpect(status().isOk());

        assertEquals(0, userActivityHistoryRepository.findAll().size());
    }

    @Test
    void testRetrieveAllHistoryForAllActivityThatUserIsFollowing() throws Exception {
        EditActivityRoleRequest request = new EditActivityRoleRequest();
        request.setRole(ActivityRole.FOLLOWER);
        userActivityService.editUserRole(noneUser.getId(), marathon.getId(), request);

        mockMvc.perform(put("/profiles/" + creatorUser.getId() + "/activities/" + marathon.getId())
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(creatorUser)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(activityEditRequest)))
                .andExpect(status().isOk());

        mockMvc.perform(put("/profiles/" + creatorUser.getId() + "/activities/" + marathon.getId())
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(creatorUser)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(activityEditRequest)))
                .andExpect(status().isOk());


        Page<SingleHistoryResponse> response = activityHistoryService.readByUserId(noneUser.getId(), PAGE, SIZE);
        assertEquals(2, response.getNumberOfElements());
    }

    @Test
    void testRetrieveAllHistoryForAllActivityThatUserIsParticipating() throws Exception {
        EditActivityRoleRequest request = new EditActivityRoleRequest();
        request.setRole(ActivityRole.PARTICIPANT);
        userActivityService.editUserRole(noneUser.getId(), marathon.getId(), request);

        mockMvc.perform(put("/profiles/" + creatorUser.getId() + "/activities/" + marathon.getId())
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(creatorUser)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(activityEditRequest)))
                .andExpect(status().isOk());

        mockMvc.perform(put("/profiles/" + creatorUser.getId() + "/activities/" + marathon.getId())
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(creatorUser)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(activityEditRequest)))
                .andExpect(status().isOk());


        Page<SingleHistoryResponse> response = activityHistoryService.readByUserId(noneUser.getId(), PAGE, SIZE);
        assertEquals(2, response.getNumberOfElements());
    }

    @Test
    void testRetrieveNoHistoryForAllActivityThatUserIsNotFollowing() throws Exception {
        mockMvc.perform(put("/profiles/" + creatorUser.getId() + "/activities/" + marathon.getId())
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(creatorUser)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(activityEditRequest)))
                .andExpect(status().isOk());

        Page<SingleHistoryResponse> response = activityHistoryService.readByUserId(noneUser.getId(), PAGE, SIZE);
        assertEquals(0, response.getNumberOfElements());

    }

    @Test
    void testCreatorShouldNotSeeOwnUpdates() throws Exception {
        mockMvc.perform(put("/profiles/" + creatorUser.getId() + "/activities/" + marathon.getId())
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(creatorUser)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(activityEditRequest)))
                .andExpect(status().isOk());

        Page<SingleHistoryResponse> response = activityHistoryService.readByUserId(creatorUser.getId(), PAGE, SIZE);
        assertEquals(0, response.getNumberOfElements());

    }

}


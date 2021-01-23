package com.springvuegradle.team200.controller;

import com.springvuegradle.team200.AbstractInitialiser;
import com.springvuegradle.team200.dto.request.ActivityAccessorRequest;
import com.springvuegradle.team200.dto.request.ActivityVisibilityRequest;
import com.springvuegradle.team200.model.ActivityRole;
import com.springvuegradle.team200.model.ActivityVisibility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.servlet.http.Cookie;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ActivityVisibilityTest extends AbstractInitialiser {


    ActivityAccessorRequest organiserRequest = new ActivityAccessorRequest("organiserUser", ActivityRole.ORGANISER);
    ActivityAccessorRequest participantRequest = new ActivityAccessorRequest("participantUser", ActivityRole.PARTICIPANT);
    ActivityAccessorRequest followerRequest = new ActivityAccessorRequest("followerUser", ActivityRole.FOLLOWER);
    List<ActivityAccessorRequest> activityAccessorRequestList = List.of(organiserRequest, participantRequest, followerRequest);

    ActivityVisibilityRequest activityVisibilityRestrictedRequest = new ActivityVisibilityRequest(ActivityVisibility.RESTRICTED, activityAccessorRequestList);


    // Not working when using this empty list as the 'accessor' of 'activityVisibilityPublicRequest' and 'activityVisibilityPrivateRequest'
    List<ActivityAccessorRequest> activityAccessorRequestEmpty = new ArrayList<>();

    ActivityVisibilityRequest activityVisibilityPrivateRequest = new ActivityVisibilityRequest(ActivityVisibility.PRIVATE, activityAccessorRequestEmpty);
    ActivityVisibilityRequest activityVisibilityPublicRequest = new ActivityVisibilityRequest(ActivityVisibility.PUBLIC, activityAccessorRequestEmpty);


    public ActivityVisibilityTest() throws ParseException {
        super();
    }


    @Test
    void testEditActivityVisibilityToRestricted() throws Exception {
        mockMvc.perform(put("/profiles/" + creatorUser.getId() + "/activities/" + marathon.getId() + "/visibility")
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(creatorUser)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(activityVisibilityRestrictedRequest)))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(ActivityRole.FOLLOWER.ordinal(), activityRepository.findActivityRole(followerUser.getId(), marathon.getId()));
        assertEquals(ActivityRole.PARTICIPANT.ordinal(), activityRepository.findActivityRole(participantUser.getId(), marathon.getId()));
        assertEquals(ActivityRole.ORGANISER.ordinal(), activityRepository.findActivityRole(organiserUser.getId(), marathon.getId()));

        assertEquals(ActivityVisibility.RESTRICTED, activityRepository.findById(marathon.getId()).get().getVisibility());
    }

    @Test
    void testEditActivityVisibilityToPrivate() throws Exception {
        mockMvc.perform(put("/profiles/" + creatorUser.getId() + "/activities/" + marathon.getId() + "/visibility")
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(creatorUser)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(activityVisibilityPrivateRequest)))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(ActivityVisibility.PRIVATE, activityRepository.findById(marathon.getId()).get().getVisibility());
    }

    @Test
    void testEditActivityVisibilityToPublic() throws Exception {
        mockMvc.perform(put("/profiles/" + creatorUser.getId() + "/activities/" + marathon.getId() + "/visibility")
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(creatorUser)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(activityVisibilityPrivateRequest)))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(ActivityVisibility.PRIVATE, activityRepository.findById(marathon.getId()).get().getVisibility());

        mockMvc.perform(put("/profiles/" + creatorUser.getId() + "/activities/" + marathon.getId() + "/visibility")
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(creatorUser)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(activityVisibilityPublicRequest)))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(ActivityVisibility.PUBLIC, activityRepository.findById(marathon.getId()).get().getVisibility());
    }


    @Test
    void testEditActivityVisibilityToRestrictedShouldSetParticipants() throws Exception {
        ActivityAccessorRequest pRequest = new ActivityAccessorRequest("participantUser", ActivityRole.PARTICIPANT);
        List<ActivityAccessorRequest> accessors = List.of(pRequest);
        ActivityVisibilityRequest request = new ActivityVisibilityRequest(ActivityVisibility.RESTRICTED, accessors);

        mockMvc.perform(put("/profiles/" + creatorUser.getId() + "/activities/" + marathon.getId() + "/visibility")
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(creatorUser)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(ActivityRole.NONE.ordinal(), activityRepository.findActivityRole(followerUser.getId(), marathon.getId()));
        assertEquals(ActivityRole.PARTICIPANT.ordinal(), activityRepository.findActivityRole(participantUser.getId(), marathon.getId()));
        assertEquals(ActivityRole.NONE.ordinal(), activityRepository.findActivityRole(organiserUser.getId(), marathon.getId()));
    }

    @Test
    void testEditActivityVisibilityToRestrictedShouldSetFollowers() throws Exception {
        ActivityAccessorRequest pRequest = new ActivityAccessorRequest("followerUser", ActivityRole.FOLLOWER);
        List<ActivityAccessorRequest> accessors = List.of(pRequest);
        ActivityVisibilityRequest request = new ActivityVisibilityRequest(ActivityVisibility.RESTRICTED, accessors);

        mockMvc.perform(put("/profiles/" + creatorUser.getId() + "/activities/" + marathon.getId() + "/visibility")
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(creatorUser)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(ActivityRole.FOLLOWER.ordinal(), activityRepository.findActivityRole(followerUser.getId(), marathon.getId()));
        assertEquals(ActivityRole.NONE.ordinal(), activityRepository.findActivityRole(participantUser.getId(), marathon.getId()));
        assertEquals(ActivityRole.NONE.ordinal(), activityRepository.findActivityRole(organiserUser.getId(), marathon.getId()));
    }

    @Test
    void testEditActivityVisibilityToRestrictedShouldSetOrganisers() throws Exception {
        ActivityAccessorRequest pRequest = new ActivityAccessorRequest("organiserUser", ActivityRole.ORGANISER);
        List<ActivityAccessorRequest> accessors = List.of(pRequest);
        ActivityVisibilityRequest request = new ActivityVisibilityRequest(ActivityVisibility.RESTRICTED, accessors);

        mockMvc.perform(put("/profiles/" + creatorUser.getId() + "/activities/" + marathon.getId() + "/visibility")
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(creatorUser)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(ActivityRole.NONE.ordinal(), activityRepository.findActivityRole(followerUser.getId(), marathon.getId()));
        assertEquals(ActivityRole.NONE.ordinal(), activityRepository.findActivityRole(participantUser.getId(), marathon.getId()));
        assertEquals(ActivityRole.ORGANISER.ordinal(), activityRepository.findActivityRole(organiserUser.getId(), marathon.getId()));
    }

    @Test
    void testEditActivityVisibilityToPrivateShouldRemoveRelatedUsers() throws Exception {
        mockMvc.perform(put("/profiles/" + creatorUser.getId() + "/activities/" + marathon.getId() + "/visibility")
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(creatorUser)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(activityVisibilityPrivateRequest)))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(ActivityRole.NONE.ordinal(), activityRepository.findActivityRole(followerUser.getId(), marathon.getId()));
        assertEquals(ActivityRole.NONE.ordinal(), activityRepository.findActivityRole(participantUser.getId(), marathon.getId()));
        assertEquals(ActivityRole.NONE.ordinal(), activityRepository.findActivityRole(organiserUser.getId(), marathon.getId()));
    }

    @Test
    void testEditFromRestrictedToPublicShouldSetParticipants() throws Exception {
        ActivityAccessorRequest pRequest = new ActivityAccessorRequest("organiserUser", ActivityRole.ORGANISER);
        List<ActivityAccessorRequest> accessors = List.of(pRequest);
        ActivityVisibilityRequest request = new ActivityVisibilityRequest(ActivityVisibility.RESTRICTED, accessors);

        mockMvc.perform(put("/profiles/" + creatorUser.getId() + "/activities/" + marathon.getId() + "/visibility")
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(creatorUser)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(ActivityRole.NONE.ordinal(), activityRepository.findActivityRole(followerUser.getId(), marathon.getId()));
        assertEquals(ActivityRole.NONE.ordinal(), activityRepository.findActivityRole(participantUser.getId(), marathon.getId()));
        assertEquals(ActivityRole.ORGANISER.ordinal(), activityRepository.findActivityRole(organiserUser.getId(), marathon.getId()));

        request.setVisibility(ActivityVisibility.PUBLIC);
        mockMvc.perform(put("/profiles/" + creatorUser.getId() + "/activities/" + marathon.getId() + "/visibility")
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(creatorUser)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(ActivityRole.NONE.ordinal(), activityRepository.findActivityRole(followerUser.getId(), marathon.getId()));
        assertEquals(ActivityRole.NONE.ordinal(), activityRepository.findActivityRole(participantUser.getId(), marathon.getId()));
        assertEquals(ActivityRole.ORGANISER.ordinal(), activityRepository.findActivityRole(organiserUser.getId(), marathon.getId()));
    }

    @Test
    void testEditFromNormalUserShouldThrowForbidden() throws Exception {
        mockMvc.perform(put("/profiles/" + creatorUser.getId() + "/activities/" + marathon.getId() + "/visibility")
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(noneUser)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(activityVisibilityPrivateRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testEditFromFollowerShouldThrowForbidden() throws Exception {
        mockMvc.perform(put("/profiles/" + creatorUser.getId() + "/activities/" + marathon.getId() + "/visibility")
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(followerUser)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(activityVisibilityPrivateRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testEditFromParticipantShouldThrowForbidden() throws Exception {
        mockMvc.perform(put("/profiles/" + creatorUser.getId() + "/activities/" + marathon.getId() + "/visibility")
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(participantUser)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(activityVisibilityPrivateRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testEditFromOrganiserShouldSucceed() throws Exception {
        mockMvc.perform(put("/profiles/" + creatorUser.getId() + "/activities/" + marathon.getId() + "/visibility")
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(organiserUser)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(activityVisibilityPrivateRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void testEditFromAdminShouldSucceed() throws Exception {
        mockMvc.perform(put("/profiles/" + creatorUser.getId() + "/activities/" + marathon.getId() + "/visibility")
                .cookie(new Cookie("token", jwtTokenUtil.generateToken(admin)))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(activityVisibilityPrivateRequest)))
                .andExpect(status().isOk());
    }
}

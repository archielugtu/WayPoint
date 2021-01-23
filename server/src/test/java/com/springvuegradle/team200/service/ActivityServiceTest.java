package com.springvuegradle.team200.service;

import com.springvuegradle.team200.AbstractInitialiser;
import com.springvuegradle.team200.model.ActivityRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ActivityServiceTest extends AbstractInitialiser {

    public ActivityServiceTest() throws ParseException {
        super();
    }

    @Test
    void testCreatorIsCreator() {
        ActivityRole role = ActivityRole.values()[activityRepository.findActivityRole(creatorUser.getId(), marathon.getId())] ;
        assertEquals(ActivityRole.CREATOR, role);
    }

    @Test
    void testOrganiserIsOrganiser() {
        ActivityRole role = ActivityRole.values()[activityRepository.findActivityRole(organiserUser.getId(), marathon.getId())] ;
        assertEquals(ActivityRole.ORGANISER, role);
    }

    @Test
    void testParticipantIsParticipant() {
        ActivityRole role = ActivityRole.values()[activityRepository.findActivityRole(participantUser.getId(), marathon.getId())] ;
        assertEquals(ActivityRole.PARTICIPANT, role);
    }

    @Test
    void testFollowerIsFollower() {
        ActivityRole role = ActivityRole.values()[activityRepository.findActivityRole(followerUser.getId(), marathon.getId())] ;
        assertEquals(ActivityRole.FOLLOWER, role);
    }

    @Test
    void testNoneIsNone() {
        ActivityRole role = ActivityRole.values()[activityRepository.findActivityRole(noneUser.getId(), marathon.getId())] ;
        assertEquals(ActivityRole.NONE, role);
    }
}

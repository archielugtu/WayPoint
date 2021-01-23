package com.springvuegradle.team200.controller;

import com.springvuegradle.team200.AbstractInitialiser;
import com.springvuegradle.team200.model.User;
import com.springvuegradle.team200.repository.UserRepository;
import com.springvuegradle.team200.service.UserActivityService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.text.ParseException;
import java.util.List;

/**
 * ActivityDeleteTest
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ActivityDeleteTest extends AbstractInitialiser {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserActivityService userActivityService;

    public ActivityDeleteTest() throws ParseException {
        super();
    }

    @Test
    void testDeleteActivityShouldRemoveOrganisers() {
        userActivityService.delete(creatorUser.getId(), marathon.getId());
        List<User> users = userRepository.findOrganisersByActivityId(marathon.getId());
        Assertions.assertEquals(0, users.size());
    }

    @Test
    void testDeleteActivityShouldRemoveFollowers() {
        userActivityService.delete(creatorUser.getId(), marathon.getId());
        List<User> users = userRepository.findFollowersByActivityId(marathon.getId());
        Assertions.assertEquals(0, users.size());
    }

    @Test
    void testDeleteActivityShouldRemoveParticipants() {
        userActivityService.delete(creatorUser.getId(), marathon.getId());
        List<User> users = userRepository.findParticipantsByActivityId(marathon.getId());
        Assertions.assertEquals(0, users.size());
    }

}

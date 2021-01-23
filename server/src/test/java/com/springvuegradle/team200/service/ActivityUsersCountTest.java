package com.springvuegradle.team200.service;

import com.springvuegradle.team200.AbstractInitialiser;
import com.springvuegradle.team200.dto.request.EditActivityRoleRequest;
import com.springvuegradle.team200.dto.response.ActivityUsersCountResponse;
import com.springvuegradle.team200.dto.response.UserRoleDTO;
import com.springvuegradle.team200.model.ActivityRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ActivityUsersCountTest extends AbstractInitialiser {

    public ActivityUsersCountTest() throws ParseException {
        super();
    }

    @Test
    void testRetrieveActivityShouldReturnCorrectNumberOfUsersAndVisibility() {
        ActivityUsersCountResponse response = userActivityService.countUsersByActivityId(marathon.getId());
        assertEquals(1, response.getTotalFollowers());
        assertEquals(1, response.getTotalParticipants());
        assertEquals(1, response.getTotalOrganisers());
        assertEquals("PUBLIC", response.getVisibility());
    }

    @Test
    void testRetrieveActivityAfterDemotingOrganiser() {
        EditActivityRoleRequest request = new EditActivityRoleRequest();
        request.setRole(ActivityRole.FOLLOWER);

        userActivityService.editUserRole(organiserUser.getId(), marathon.getId(), request);

        ActivityUsersCountResponse response = userActivityService.countUsersByActivityId(marathon.getId());
        assertEquals(2, response.getTotalFollowers());
        assertEquals(1, response.getTotalParticipants());
        assertEquals(0, response.getTotalOrganisers());
    }

    @Test
    void testRetrieveAllUsersCount() {
        Page<UserRoleDTO> response = userActivityService.readAllUsersRelatedToActivity(marathon.getId(), 0, 100);
        assertEquals(3, response.getContent().size());
    }
}

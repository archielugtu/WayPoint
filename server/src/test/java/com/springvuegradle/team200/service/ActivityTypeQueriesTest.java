package com.springvuegradle.team200.service;

import com.springvuegradle.team200.dto.request.SearchUserRequest;
import com.springvuegradle.team200.dto.response.SearchUserResponse;
import com.springvuegradle.team200.model.ActivityType;
import com.springvuegradle.team200.model.User;
import com.springvuegradle.team200.model.UserEmail;
import com.springvuegradle.team200.repository.ActivityTypesRepository;
import com.springvuegradle.team200.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ActivityTypeQueriesTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ActivityTypesRepository activityTypesRepository;

    private SearchUserRequest searchUserRequest;

    private User bob;

    private User fred;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();

        searchUserRequest = new SearchUserRequest();

        bob = new User();
        fred = new User();
        User jim = new User();
        User mandy = new User();

        setupSingleUser(bob, "Bob", List.of("Quad Biking", "Airboarding", "WaterWalkerz water sphering"));
        setupSingleUser(fred, "Fred", List.of("Airboarding"));
        setupSingleUser(jim, "Jim", List.of("Running"));
        setupSingleUser(mandy, "Mandy", List.of());
    }

    void setupSingleUser(User user, String name, List<String> activityTypes) {
        List<ActivityType> activityTypeList = new ArrayList<>();
        for (String s : activityTypes) {
            activityTypeList.add(activityTypesRepository.findByType(s).get());
        }
        user.setActivityTypes(activityTypeList);
        user.setFirstName(name);
        user.setLastName(name);
        user.setUserEmails(List.of(new UserEmail(name + "@" + name + ".com", user, true)));
        userRepository.save(user);
    }

    @Test
    void testRetrieveActivityUncommonToEveryoneShouldReturnEmptyPage() {
        searchUserRequest.setActivities("Soccer");
        searchUserRequest.setMethod("or");
        Page<SearchUserResponse> result = userService.readByQuery(10, searchUserRequest, bob);
        assertEquals(0, result.getTotalElements());

        searchUserRequest.setMethod("and");
        result = userService.readByQuery(10, searchUserRequest, bob);
        assertEquals(0, result.getTotalElements());
    }

    @Test
    void testRetrieveCommonActivityTypeWithORShouldReturnCorrectUsers() {
        searchUserRequest.setMethod("or");
        searchUserRequest.setActivities("Airboarding");
        Page<SearchUserResponse> foundUsers = userService.readByQuery(10, searchUserRequest, bob);
        assertEquals(1, foundUsers.getTotalElements());

        List<String> foundNames = foundUsers.stream()
                .map(SearchUserResponse::getFirstName)
                .collect(Collectors.toList());

        assertTrue(foundNames.contains("Fred"));
    }

    @Test
    void testRetrieveCommonActivityTypeWithANDShouldReturnCorrectUsers() {
        searchUserRequest.setMethod("and");
        searchUserRequest.setActivities("Airboarding");

        Page<SearchUserResponse> foundUsers = userService.readByQuery(10, searchUserRequest,bob);
        assertEquals(1, foundUsers.getTotalElements());

        List<String> foundNames = foundUsers.stream()
                .map(SearchUserResponse::getFirstName)
                .collect(Collectors.toList());

        assertTrue(foundNames.contains("Fred"));
    }

    @Test
    void testRetrieveDifferentActivityTypeWithANDShouldReturnCorrectUsers() {
        searchUserRequest.setActivities("Airboarding%20Quad Biking");
        searchUserRequest.setMethod("and");
        Page<SearchUserResponse> foundUsers =
                userService.readByQuery(10, searchUserRequest, fred);
        assertEquals(1, foundUsers.getTotalElements());

        List<String> foundNames = foundUsers.stream()
                .map(SearchUserResponse::getFirstName)
                .collect(Collectors.toList());

        assertTrue(foundNames.contains("Bob"));
    }

    @Test
    void testRetrieveDifferentActivityTypeWithORShouldReturnCorrectUsers() {
        searchUserRequest.setActivities("Running%20Quad Biking");
        searchUserRequest.setMethod("or");
        Page<SearchUserResponse> foundUsers =
                userService.readByQuery(10, searchUserRequest, fred);
        assertEquals(2, foundUsers.getTotalElements());

        List<String> foundNames = foundUsers.stream()
                .map(SearchUserResponse::getFirstName)
                .collect(Collectors.toList());

        assertTrue(foundNames.contains("Bob"));
        assertTrue(foundNames.contains("Jim"));
    }

    @Test
    void testRetrieveNoActivitiesShouldReturnNoUsers() {
        searchUserRequest.setActivities("");
        searchUserRequest.setMethod("or");
        Page<SearchUserResponse> foundUsers =
                userService.readByQuery(10, searchUserRequest, bob);
        assertEquals(0, foundUsers.getTotalElements());
    }
}

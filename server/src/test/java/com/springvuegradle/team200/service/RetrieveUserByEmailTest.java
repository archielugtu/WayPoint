package com.springvuegradle.team200.service;

import com.springvuegradle.team200.dto.response.UserAdminStatusResponse;
import com.springvuegradle.team200.model.User;
import com.springvuegradle.team200.model.UserEmail;
import com.springvuegradle.team200.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RetrieveUserByEmailTest {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    private User fabian;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
        fabian = createUser("Fabian", "Gilson");
        fabian.setIsAdmin(true);
        userRepository.save(fabian);

        createUser("Gabian", "Filson");
    }

    private User createUser(String firstName, String lastName) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(firstName.substring(0, 3));
        user.setMiddleName(lastName.substring(0, 3));
        UserEmail fabianEmail = new UserEmail(
                firstName + "@" + lastName + ".me", user, true
        );
        user.setUserEmails(new ArrayList<>(Collections.singletonList(fabianEmail)));
        userRepository.save(user);
        return user;
    }

    @Test
    void testPassingNullShouldReturnNoResult() {
        assertEquals(List.of(), userService.readAdminStatusByEmail(null));
    }

    @Test
    void testPassingEmptyListShouldReturnNoResult() {
        assertEquals(List.of(), userService.readAdminStatusByEmail(List.of()));
    }

    @Test
    void testPassingEmailsListShouldReturnCorrectNumberOfUsers() {
        List<UserAdminStatusResponse> userAdminStatusResponses =
                userService.readAdminStatusByEmail(List.of("Fabian@Gilson.me", "Gabian@Filson.me"));

        assertEquals(2, userAdminStatusResponses.size());
    }

    @Test
    void testPassingEmailsListShouldReturnCorrectId() {
        List<UserAdminStatusResponse> userAdminStatusResponses =
                userService.readAdminStatusByEmail(List.of("Fabian@Gilson.me"));

        assertEquals(1, userAdminStatusResponses.size());
        UserAdminStatusResponse response = userAdminStatusResponses.get(0);
        assertEquals(fabian.getId(), response.getUserId());
    }

    @Test
    void testPassingEmailsListShouldReturnCorrectIsAdminStatus() {
        List<UserAdminStatusResponse> userAdminStatusResponses =
                userService.readAdminStatusByEmail(List.of("Fabian@Gilson.me"));

        assertEquals(1, userAdminStatusResponses.size());
        UserAdminStatusResponse response = userAdminStatusResponses.get(0);
        assertEquals(fabian.getIsAdmin(), response.isAdmin());
    }
}

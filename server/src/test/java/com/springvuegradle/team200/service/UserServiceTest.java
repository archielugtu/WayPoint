package com.springvuegradle.team200.service;

import com.springvuegradle.team200.AbstractInitialiser;
import com.springvuegradle.team200.dto.request.SearchUserRequest;
import com.springvuegradle.team200.dto.response.SearchUserResponse;
import com.springvuegradle.team200.model.User;
import com.springvuegradle.team200.model.UserEmail;
import com.springvuegradle.team200.repository.PhotoRepository;
import com.springvuegradle.team200.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserServiceTest extends AbstractInitialiser {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    private SearchUserRequest searchUserRequest;

    private List<User> users;

    private User fabian;

    public UserServiceTest() throws ParseException {
        super();
    }

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
        users = new ArrayList<>();
        fabian = createUser("Fabian", "Gilson");
        users.add(fabian);
        users.add(createUser("Gabian", "Filson"));
        users.add(createUser("Flinstone", "Matthews"));

        searchUserRequest = new SearchUserRequest();
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
    void readAllTestShouldReturnNoUsers() {
        Page<SearchUserResponse> response = userService.readByQuery(5, searchUserRequest, fabian);
        assertEquals(0, response.getTotalElements());
    }

    @Test
    void readAllWithSizeLimitShouldReturnItemsWithinSizeLimit() {
        searchUserRequest.setFirstname("Gabian");
        Page<SearchUserResponse> response = userService.readByQuery(1, searchUserRequest, fabian);
        assertEquals(1, response.getContent().size());
    }

    @Test
    void readAllWithInvalidPageNumberShouldReturnEmptyPage() {
        searchUserRequest.setPage(10);
        Page<SearchUserResponse> response = userService.readByQuery(100, searchUserRequest, fabian);
        assertEquals(0, response.getNumberOfElements());
    }

    @ParameterizedTest
    @ValueSource(strings = {"Gabian", "gabian"})
    void readAllByFirstnameShouldReturnUsersWithFirstname(String firstNameQuery) {
        searchUserRequest.setFirstname(firstNameQuery);
        Page<SearchUserResponse> response = userService.readByQuery(100, searchUserRequest, fabian);
        assertEquals(1, response
                .filter(r -> r.getFirstName().toLowerCase().equals(firstNameQuery.toLowerCase()))
                .toList()
                .size());
        assertEquals(1, response.getTotalElements());
    }

    @ParameterizedTest
    @ValueSource(strings = {"Filson", "filson"})
    void readAllByLastNameShouldReturnUsersWithLastName(String lastNameQuery) {
        searchUserRequest.setLastname(lastNameQuery);
        Page<SearchUserResponse> response = userService.readByQuery(100, searchUserRequest, fabian);
        assertEquals(1, response
                .filter(r -> r.getLastName().toLowerCase().equals(lastNameQuery.toLowerCase()))
                .toList()
                .size());
        assertEquals(1, response.getTotalElements());
    }

    @ParameterizedTest
    @ValueSource(strings = {"flinstone@matthews.me", "flinSTONE@matthews.ME"})
    void readAllByEmailShouldReturnUsersWithEmail(String emailQuery) {
        searchUserRequest.setEmail(emailQuery);
        Page<SearchUserResponse> response = userService.readByQuery(100, searchUserRequest, fabian);
        assertEquals(1, response
                .filter(r -> r.getPrimaryEmail().toLowerCase().equals(emailQuery.toLowerCase()))
                .toList()
                .size());
        assertEquals(1, response.getTotalElements());
    }
}

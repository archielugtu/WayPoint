package com.springvuegradle.team200.controller;

import com.jayway.jsonpath.JsonPath;
import com.springvuegradle.team200.jwt.JwtTokenUtil;
import com.springvuegradle.team200.model.ActivityType;
import com.springvuegradle.team200.model.User;
import com.springvuegradle.team200.model.UserEmail;
import com.springvuegradle.team200.repository.ActivityTypesRepository;
import com.springvuegradle.team200.repository.UserEmailRepository;
import com.springvuegradle.team200.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.servlet.http.Cookie;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserSearchTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ActivityTypesRepository activityTypesRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserEmailRepository userEmailRepository;

    @Autowired
    private JwtTokenUtil tokenUtil;

    private String jackToken;
    private String jeromeToken;
    private String davidToken;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
        userEmailRepository.deleteAll();

        User jack = new User();
        User jerome = new User();
        User david1 = new User();
        User david = new User();
        setupSingleUser(jerome, "Jerome", "Grubb", List.of("Quad Biking", "Airboarding", "WaterWalkerz water sphering"));
        setupSingleUser(jack, "Jack", "Craig", List.of("Running"));
        setupSingleUser(david,"david","turton",List.of());
        setupSingleUser(david1,"1david","turton123",List.of());

        jackToken = tokenUtil.generateToken(jack);
        jeromeToken = tokenUtil.generateToken(jerome);
        davidToken = tokenUtil.generateToken(david);
    }

    void setupSingleUser(User user, String name, String lName, List<String> activityTypes) {
        List<ActivityType> activityTypeList = new ArrayList<>();
        for (String s : activityTypes) {
            activityTypeList.add(activityTypesRepository.findByType(s).get());
        }
        user.setActivityTypes(activityTypeList);
        user.setUsername(name);
        user.setFirstName(name);
        user.setLastName(lName);
        user.setUserEmails(List.of(new UserEmail(name + "@" + "test.com", user, true)));
        userRepository.save(user);
    }

    @Test
    void testSearchWithEmptyFieldShouldReturnNoUsers() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/profiles")
                .cookie(new Cookie("token", jackToken))
                .param("page", "0")
                .param("firstname", "")
                .param("lastname", "")
                .param("email", ""))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertEquals(0, JsonPath.parse(response).read("$.totalElements", Integer.class));
    }

    @Test
    void testSearchEmailExisting() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/profiles")
                .cookie(new Cookie("token", jeromeToken))
                .param("email", "Jack@test.com"))
                .andExpect(status().isOk())
                .andReturn();


        String response = mvcResult.getResponse().getContentAsString();
        assertEquals("Jack", JsonPath.parse(response).read("$.results[0].firstname"));
    }

    @Test
    void testSearchEmailExistingPartial() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/profiles")
                .cookie(new Cookie("token", jeromeToken))
                .param("email", "Jack@t"))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertEquals(1, JsonPath.parse(response).read("$.totalElements", Integer.class));
    }

    @Test
    void testSearchEmailNonExisting() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/profiles")
                .cookie(new Cookie("token", jeromeToken))
                .param("email", "WWWWWWWWWWWW"))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertEquals(0, JsonPath.parse(response).read("$.totalElements", Integer.class));
    }

    @Test
    void testSearchFirstNameExisting() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/profiles")
                .cookie(new Cookie("token", jackToken))
                .param("firstname", "jerome"))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertEquals("Jerome", JsonPath.parse(response).read("$.results[0].firstname"));
        assertEquals("Grubb", JsonPath.parse(response).read("$.results[0].lastname"));

    }

    @Test
    void testSearchFirstNamePartial() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/profiles")
                .cookie(new Cookie("token", jackToken))
                .param("firstname", "jero"))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertEquals("Jerome", JsonPath.parse(response).read("$.results[0].firstname"));
        assertEquals("Grubb", JsonPath.parse(response).read("$.results[0].lastname"));
    }

    @Test
    void testSearchFirstNameNonExisting() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/profiles")
                .cookie(new Cookie("token", jeromeToken))
                .param("firstname", "WWWWWWWWWWW"))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertEquals(0, JsonPath.parse(response).read("$.totalElements", Integer.class));
    }

    @Test
    void testSearchLastNameExisting() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/profiles")
                .cookie(new Cookie("token", jackToken))
                .param("lastname", "grubb"))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertEquals("Jerome", JsonPath.parse(response).read("$.results[0].firstname"));
        assertEquals("Grubb", JsonPath.parse(response).read("$.results[0].lastname"));
    }

    @Test
    void testSearchLastNamePartial() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/profiles")
                .cookie(new Cookie("token", jackToken))
                .param("lastname", "gru"))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertEquals("Jerome", JsonPath.parse(response).read("$.results[0].firstname"));
        assertEquals("Grubb", JsonPath.parse(response).read("$.results[0].lastname"));
    }

    @Test
    void testSearchLastNameNonExisting() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/profiles")
                .cookie(new Cookie("token", jeromeToken))
                .param("lastname", "wwwwwwwwwwwwww"))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertEquals(0, JsonPath.parse(response).read("$.totalElements", Integer.class));
    }

    @Test
    void testSearchActivitiesAnd() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/profiles")
                .cookie(new Cookie("token", davidToken))
                .param("activities", "Quad Biking%20Airboarding")
                .param("method", "and"))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertEquals("Jerome", JsonPath.parse(response).read("$.results[0].firstname"));
        assertEquals("Grubb", JsonPath.parse(response).read("$.results[0].lastname"));
    }

    @Test
    void testSearchActivitiesOr() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/profiles")
                .cookie(new Cookie("token", davidToken))
                .param("activities", "Quad Biking%20Running")
                .param("method", "or"))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertEquals("Jerome", JsonPath.parse(response).read("$.results[1].firstname"));
        assertEquals("Grubb", JsonPath.parse(response).read("$.results[1].lastname"));

        assertEquals("Jack", JsonPath.parse(response).read("$.results[0].firstname"));
        assertEquals("Craig", JsonPath.parse(response).read("$.results[0].lastname"));
    }

    @Test
    void testSearchActivitiesAndNotFound() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/profiles")
                .cookie(new Cookie("token", jeromeToken))
                .param("activities", "Quad Biking%20Running")
                .param("method", "and"))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertEquals(0, JsonPath.parse(response).read("$.totalElements", Integer.class));

    }

    @Test
    void testSearchActivitiesAndName() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/profiles")
                .cookie(new Cookie("token", davidToken))
                .param("activities", "Quad Biking")
                .param("firstname", "Jerome")
                .param("method", "and"))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertEquals("Jerome", JsonPath.parse(response).read("$.results[0].firstname"));
        assertEquals("Grubb", JsonPath.parse(response).read("$.results[0].lastname"));

    }

    @Test
    void testSearchActivitiesAndLastName() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/profiles")
                .cookie(new Cookie("token", davidToken))
                .param("activities", "Quad Biking")
                .param("lastname", "Grubb")
                .param("method", "and"))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertEquals("Jerome", JsonPath.parse(response).read("$.results[0].firstname"));
        assertEquals("Grubb", JsonPath.parse(response).read("$.results[0].lastname"));

    }

    @Test
    void testSearchActivitiesAndEmail() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/profiles")
                .cookie(new Cookie("token", davidToken))
                .param("activities", "Quad Biking")
                .param("email", "jerome@test.com")
                .param("method", "and"))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertEquals("Jerome", JsonPath.parse(response).read("$.results[0].firstname"));
        assertEquals("Grubb", JsonPath.parse(response).read("$.results[0].lastname"));

    }

    @Test
    void testSearchActivitiesAndNameInvalid() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/profiles")
                .cookie(new Cookie("token", davidToken))
                .param("activities", "Running")
                .param("firstname", "jerome")
                .param("method", "and"))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertEquals(0, JsonPath.parse(response).read("$.totalElements", Integer.class));
    }


    @Test
    void testSearchWithInvalidPageShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/profiles")
                .cookie(new Cookie("token", jeromeToken))
                .param("activities", "Running")
                .param("page", "-1"))
                .andExpect(status().isBadRequest());
    }
    @ParameterizedTest
    @ValueSource(strings = {"'david'", "\"david\""})
    void testFirstNameWithQuotes(String firstname) throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/profiles")
                .cookie(new Cookie("token", jeromeToken))
                .param("firstname", firstname))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertEquals(1, JsonPath.parse(response).read("$.totalElements", Integer.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {"'turton'", "\"turton\""})
    void testLastNameWithQuotes(String lastname) throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/profiles")
                .cookie(new Cookie("token", jeromeToken))
                .param("lastname", lastname))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertEquals(1, JsonPath.parse(response).read("$.totalElements", Integer.class));
    }
    @ParameterizedTest
    @ValueSource(strings = {"'david@test.com'", "\"david@test.com\""})
    void testEmailWithQuotes(String email) throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/profiles")
                .cookie(new Cookie("token", jeromeToken))
                .param("email", email))
                .andExpect(status().isOk())
                .andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        assertEquals(1, JsonPath.parse(response).read("$.totalElements", Integer.class));
    }
    @ParameterizedTest
    @ValueSource(strings = {"\"david@test.com'", "\'david@test.com\"", "'","'david@test.com","\"david@test.com",})
    void testEmailWithIncorrectQuotes(String email) throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/profiles")
                .cookie(new Cookie("token", jeromeToken))
                .param("email", email))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertEquals(0, JsonPath.parse(response).read("$.totalElements", Integer.class));
    }
    @ParameterizedTest
    @ValueSource(strings = {"\"david'", "\'david\"", "'","'david","\"david",})
    void testFirstNameWithIncorrectQuotes(String firstname) throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/profiles")
                .cookie(new Cookie("token", jeromeToken))
                .param("firstname", firstname))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertEquals(0, JsonPath.parse(response).read("$.totalElements", Integer.class));
    }
    @ParameterizedTest
    @ValueSource(strings = {"\"turton'", "\'turton\"", "'","'turton","\"turton",})
    void testLastNameWithIncorrectQuotes(String lastname) throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/profiles")
                .cookie(new Cookie("token", jeromeToken))
                .param("lastname", lastname))
                .andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertEquals(0, JsonPath.parse(response).read("$.totalElements", Integer.class));
    }

}
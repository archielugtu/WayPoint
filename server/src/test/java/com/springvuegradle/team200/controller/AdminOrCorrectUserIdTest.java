package com.springvuegradle.team200.controller;

import com.springvuegradle.team200.AbstractInitialiser;
import com.springvuegradle.team200.jwt.JwtTokenUtil;
import com.springvuegradle.team200.model.User;
import com.springvuegradle.team200.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.Cookie;
import java.text.ParseException;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminOrCorrectUserIdTest extends AbstractInitialiser {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    private String jackToken;
    private String adminToken;
    private Long jackId;
    private Long kevinId;

    public AdminOrCorrectUserIdTest() throws ParseException {
        super();
    }

    @BeforeEach
    void setup() {
        userRepository.deleteAll();

        User admin = new User("admin", passwordEncoder.encode("password0"));
        admin.setIsAdmin(true);
        User jack = new User("jack", passwordEncoder.encode("password1"));
        User kevin = new User("kevin", passwordEncoder.encode("password2"));
        userRepository.saveAll(List.of(admin, jack, kevin));

        jackToken = jwtTokenUtil.generateToken(jack);
        adminToken = jwtTokenUtil.generateToken(admin);
        jackId = jack.getId();
        kevinId = kevin.getId();
    }

    @Test
    void testRequestFromCorrectNonAdminUser() throws Exception {
        mockMvc.perform(get("/Debug/userOrAdmin/" + jackId)
                .cookie(new Cookie("token", jackToken)))
                .andExpect(status().isOk());
    }

    @Test
    void testRequestFromIncorrectNonAdminUser() throws Exception {
        mockMvc.perform(get("/Debug/userOrAdmin/" + kevinId)
                .cookie(new Cookie("token", jackToken)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testRequestFromAdminUser() throws Exception {
        mockMvc.perform(get("/Debug/userOrAdmin/" + kevinId)
                .cookie(new Cookie("token", adminToken)))
                .andExpect(status().isOk());
    }

    @Test
    void testRequestFromNoToken() throws Exception {
        mockMvc.perform(get("/Debug/userOrAdmin/" + kevinId))
                .andExpect(status().isUnauthorized());
    }
}

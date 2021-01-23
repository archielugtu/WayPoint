package com.springvuegradle.team200.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springvuegradle.team200.dto.request.RoleRequest;
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
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminOnlyTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
    private Long rchiId;

    private RoleRequest roleRequest;

    @BeforeEach
    void setup() throws Exception {
        userRepository.deleteAll();

        User admin = new User("admin", passwordEncoder.encode("password0"));
        admin.setIsAdmin(true);
        User jack = new User("jack", passwordEncoder.encode("password1"));
        User kevin = new User("kevin", passwordEncoder.encode("password2"));
        User rchi = new User("rchi", passwordEncoder.encode("password3"));
        userRepository.saveAll(List.of(admin, jack, kevin, rchi));

        jackToken = jwtTokenUtil.generateToken(jack);
        adminToken = jwtTokenUtil.generateToken(admin);
        jackId = jack.getId();
        kevinId = kevin.getId();
        rchiId = rchi.getId();

        roleRequest = new RoleRequest();
        roleRequest.setRole("admin");
    }

    @Test
    void testRequestFromCorrectNonAdminUser() throws Exception {
        mockMvc.perform(get("/Debug/adminOnly/" + jackId)
                .cookie(new Cookie("token", jackToken)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testRequestFromIncorrectNonAdminUser() throws Exception {
        mockMvc.perform(get("/Debug/adminOnly/" + kevinId)
                .cookie(new Cookie("token", jackToken)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testRequestFromAdminUser() throws Exception {
        mockMvc.perform(get("/Debug/adminOnly/" + kevinId)
                .cookie(new Cookie("token", adminToken)))
                .andExpect(status().isOk());
    }

    @Test
    void testRequestFromNoToken() throws Exception {
        mockMvc.perform(get("/Debug/adminOnly/" + kevinId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testAdminDeleteUser() throws Exception {
        mockMvc.perform(delete("/profiles/" + rchiId)
                .cookie(new Cookie("token", adminToken)))
                .andExpect(status().isOk());
    }

    @Test
    void testAdminDeleteUserTwice() throws Exception {
        mockMvc.perform(delete("/profiles/" + rchiId)
                .cookie(new Cookie("token", adminToken)))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/profiles/" + rchiId)
                .cookie(new Cookie("token", adminToken)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testAdminChangeUserRole() throws Exception {
        mockMvc.perform(put("/profiles/" + rchiId + "/role")
                .cookie(new Cookie("token", adminToken))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(roleRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void testAdminChangeUserRoleToEmptyString() throws Exception {
        roleRequest.setRole("");
        mockMvc.perform(put("/profiles/" + rchiId + "/role")
                .cookie(new Cookie("token", adminToken))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(roleRequest)))
                .andExpect(status().isBadRequest());
    }
}

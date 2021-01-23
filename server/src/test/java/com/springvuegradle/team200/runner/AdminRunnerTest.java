package com.springvuegradle.team200.runner;

import com.springvuegradle.team200.model.User;
import com.springvuegradle.team200.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * This test uses actual database in purpose
 * to check if a global admin exists
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class AdminRunnerTest {

    @Autowired
    UserRepository userRepository;

    @Test
    void testRunnerCreatesAdminUser() {
        User u = userRepository.findByIsGlobalAdminTrue();
        assertNotNull(u);
        assertNotNull(u.getPassword());
        assertNotNull(u.getUserEmails());
    }
}

package com.springvuegradle.team200.steps;

import com.springvuegradle.team200.AbstractCucumber;
import io.cucumber.java.en.Given;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import javax.transaction.Transactional;
import java.text.ParseException;

@SpringBootTest
@ContextConfiguration
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SharedSteps extends AbstractCucumber {

    public SharedSteps() throws ParseException {
        super();
    }

    // Put this at the start of any cucumber cases as there can only be one class with @SpingBootTest
    // DO NOT DELETE
    @Given("I initialise the repositories")
    @Transactional
    public void setup() {
        userRepository.deleteAllByIsGlobalAdminFalse();
        activityRepository.deleteAll();
    }
}

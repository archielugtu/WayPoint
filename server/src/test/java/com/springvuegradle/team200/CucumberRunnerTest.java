package com.springvuegradle.team200;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        plugin = {"pretty"}, // How to format test report, "pretty" is good for human eyes
        glue = {"com/springvuegradle/team200/steps"}, // Where to look for your tests' steps
        features = {"src/test/resources/features"}, // Where to look for your features
        strict = false // Causes cucumber to fail if any step definitions are still undefined
)

// Cucumber options are defined in build.gradle
public class CucumberRunnerTest {

} // Classname ends with "Test" so it will be picked up by JUnit and hence by 'gradle test'


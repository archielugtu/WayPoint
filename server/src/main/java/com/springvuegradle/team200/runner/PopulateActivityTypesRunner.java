package com.springvuegradle.team200.runner;

import com.springvuegradle.team200.exception.ResourceNotFoundException;
import com.springvuegradle.team200.exception.ResourceUnreadableException;
import com.springvuegradle.team200.model.ActivityType;
import com.springvuegradle.team200.repository.ActivityTypesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Populates the activity types database with data loaded from seeds/activityTypes.txt
 * in the resources directory
 * <p>
 * This class is automatically run during startup
 */
@Component
public class PopulateActivityTypesRunner implements CommandLineRunner {

    @Autowired
    ActivityTypesRepository activityTypesRepository;

    @Override
    public void run(String... args) throws Exception {
        // Find the file seeds/activityTypes.txt in the resources directory
        String activityTypesFile = "seeds/activityTypes.txt";

        ClassLoader classLoader = this.getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(activityTypesFile);
        if (inputStream == null) {
            // Doesnt exist
            throw new ResourceNotFoundException(activityTypesFile);
        }
        // Save each activity into the database
        // Create a new buffered reader, wrap in try-with-resources, to ensure stream is closed.
        try (Stream<String> lines = new BufferedReader(new InputStreamReader(inputStream)).lines()) {
            lines.forEach(
                    line -> {
                        Optional<ActivityType> existing = activityTypesRepository.findByType(line);
                        if (existing.isEmpty()) { // Add to database if it doesn't
                            ActivityType a = new ActivityType();
                            a.setType(line);
                            activityTypesRepository.save(a);
                        }
                    });
        } catch (Exception e) {
            throw new ResourceUnreadableException(activityTypesFile);
        }
    }
}

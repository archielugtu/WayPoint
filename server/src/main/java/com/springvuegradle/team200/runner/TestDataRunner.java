package com.springvuegradle.team200.runner;

import com.springvuegradle.team200.exception.UnableHashPasswordException;
import com.springvuegradle.team200.model.*;
import com.springvuegradle.team200.repository.ActivityRepository;
import com.springvuegradle.team200.repository.UserRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.transaction.Transactional;
import javax.xml.bind.DatatypeConverter;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Runs during Spring bootup and creates placeholder Users and Activities
 */
@Component
@Transactional
public class TestDataRunner implements CommandLineRunner {

    protected final Log logger = LogFactory.getLog(getClass());

    @Value("${isDevMode:false}")
    private boolean isDevMode;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String hashedPassword;

    private User millhouse;
    private User lionel;
    private User edna;
    private User seymour;
    private User gary;
    private User hank;
    private Activity funRun;
    private Activity steamedHams;

    /**
     * Generates set of test users and activities for development purposes
     *
     * @param args Runner arguments
     */
    @Override
    public void run(String... args) {
        if (!isDevMode) {
            return;
        }

        // Hash password twice to simulate frontend and backend hashing
        String rawPassword = "password";
        String frontEndHashedPassword = DatatypeConverter.printHexBinary(hashPassword(rawPassword));
        hashedPassword = passwordEncoder.encode(frontEndHashedPassword);

        fillTemplateUsers();

        // Populate Existing users that do not exist
        for (var user : List.of(millhouse, lionel, edna, seymour, gary, hank)) {
            if (userRepository.getUserFromUsername(user.getUsername()) == null) {
                userRepository.save(user);
                logger.info("DevMode user created: " + user.getPrimaryEmail().getAddress() + "/" + rawPassword);
            }
        }

        // Populate Activities
        for (var activity : List.of(funRun, steamedHams)) {
            if (activityRepository.findByActivityName(activity.getActivityName()).isEmpty()) {
                activityRepository.save(activity);
                logger.info("DevMode activity created: " + activity.getActivityName());
            }
        }
    }

    /**
     * Hash password using PBKDF2 method which is the same as frontend
     *
     * @param password Password to be hashed
     * @return Byte array of the hash
     */
    private byte[] hashPassword(String password) {
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), "superSalty".getBytes(), 1000, 8 * 8);
            SecretKey key = skf.generateSecret(spec);
            return key.getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new UnableHashPasswordException();
        }
    }

    /**
     * Generates List of template users to populate website for dev purposes
     */
    private void fillTemplateUsers() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        Date startDate = new Date();
        Date endDate = new Date();
        Date creationDate = new Date();

        try {
            startDate = formatter.parse("2020-11-20T08:00:00+1300");
            endDate = formatter.parse("2020-12-20T08:00:00+1300");
            creationDate = formatter.parse("2020-01-01T08:00:00+1300");
        } catch (Exception e) {
            logger.info("Failed to parse dates for test data runner");
        }

        millhouse = new User(
                "millhouse@dev.com",
                hashedPassword,
                "Millhouse",
                "Mussolini",
                "Van Houten",
                "Everything's coming up Millhouse!",
                "Male",
                "Thrillhouse",
                "1999-07-1",
                1);

        lionel = new User(
                "lionel@dev.com",
                hashedPassword,
                "Lionel",
                "",
                "Hutz",
                "Works on contingency? No, money down!",
                "Male",
                "Law-talking guy",
                "1960-07-10",
                2);

        edna = new User(
                "edna@dev.com",
                hashedPassword,
                "Edna",
                "",
                "krabappel",
                "Ha.",
                "Female",
                "Krab Apples",
                "1970-07-10",
                3);

        seymour = new User(
                "seymour@dev.com",
                hashedPassword,
                "Seymour",
                "W.",
                "Skinner",
                "Delightfully Devilish",
                "Male",
                "Skinner",
                "1970-07-10",
                4);

        gary = new User(
                "gary@dev.com",
                hashedPassword,
                "Gary",
                "",
                "Chalmers",
                "Is that smoke coming out of your oven?",
                "Male",
                "Super Nintendo Chalmers",
                "1970-07-10",
                2);

        hank = new User(
                "hank@dev.com",
                hashedPassword,
                "Hank",
                "",
                "Scorpio",
                "When you go home tonight there's gonna be another storey on your house.",
                "Male",
                "Scorpio",
                "1962-07-10",
                5);
        var lionelEmails = lionel.getUserEmails();
        lionelEmails.add(new UserEmail("miguel@dev.com", lionel, false));
        lionel.setUserEmails(lionelEmails);

        funRun = new Activity(
                "Fun Run",
                "(Not actually fun)",
                new Location(),
                false,
                startDate,
                endDate, creationDate,
                ActivityVisibility.PUBLIC);
        funRun.setCreator(hank);
        funRun.setParticipants(Set.of(hank, edna));

        steamedHams = new Activity(
                "Steamed hams",
                "May I see it?",
                new Location(),
                true,
                startDate,
                endDate, creationDate,
                ActivityVisibility.PUBLIC
                );
        steamedHams.setCreator(seymour);
        steamedHams.setParticipants(Set.of(seymour, gary));
    }
}

package com.springvuegradle.team200.runner;

import com.springvuegradle.team200.exception.UnableHashPasswordException;
import com.springvuegradle.team200.model.User;
import com.springvuegradle.team200.model.UserEmail;
import com.springvuegradle.team200.repository.UserRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Runs during Spring boot up
 * Checks the database if an admin user exist, if not create one
 */
@Component
@Transactional
public class AdminUserRunner implements CommandLineRunner {

    protected final Log logger = LogFactory.getLog(getClass());

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws NoSuchAlgorithmException {
        // Check if we have an admin user in the DB
        User admin = userRepository.findByIsGlobalAdminTrue();

        boolean hasGlobalAdmin = admin != null;

        // Make a new admin user if it does not exist yet
        if (!hasGlobalAdmin) {
            logger.info("---- GENERATING A NEW ADMIN ACCOUNT ----");
            admin = new User();
            admin.setFirstName("Global");
            admin.setLastName("Admin");

            String globalAdminEmail = "admin@admin.com";
            String globalAdminPassword = "";

            // Setup their email
            List<UserEmail> emails = new ArrayList<>();
            emails.add(new UserEmail(globalAdminEmail, admin, true));
            admin.setUserEmails(emails);

            // Setup their privileges
            admin.setIsAdmin(true);
            admin.setIsGlobalAdmin(true);

            // Setup password
            globalAdminPassword = generateRandomString();

            String hashedPasssword = DatatypeConverter.printHexBinary(hashPassword(globalAdminPassword));
            String encryptedPassword = passwordEncoder.encode(hashedPasssword);
            admin.setPassword(encryptedPassword);

            // Save it to DB
            userRepository.save(admin);

            // Print out the credentials
            logger.info("----- ADMIN USER CREDENTIALS ----");
            logger.info("* ADMIN USERNAME: " + globalAdminEmail);
            logger.info("* ADMIN PASSWORD: " + globalAdminPassword);
            logger.info("----- ADMIN USER CREDENTIALS ----");
        }
    }

    /**
     * Generates a random string between ASCII 0 and Z
     *
     * @return Random string containing alphanumeric characters
     */
    private String generateRandomString() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replace("-", "");
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
}

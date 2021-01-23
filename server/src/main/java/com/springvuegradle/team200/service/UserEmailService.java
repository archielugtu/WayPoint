package com.springvuegradle.team200.service;

import com.springvuegradle.team200.exception.EmailAlreadyRegisteredException;
import com.springvuegradle.team200.exception.EmailRegisteredWithAnotherUserException;
import com.springvuegradle.team200.exception.MaxEmailCountReachedException;
import com.springvuegradle.team200.exception.UserNotFoundException;
import com.springvuegradle.team200.model.User;
import com.springvuegradle.team200.model.UserEmail;
import com.springvuegradle.team200.repository.UserEmailRepository;
import com.springvuegradle.team200.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserEmailService {

    private static final int MAX_EMAIL_COUNT = 5;

    @Autowired
    UserEmailRepository userEmailRepository;

    @Autowired
    UserRepository userRepository;

    /**
     * Checks and update the emails associated to the user,
     * but does NOT yet save the user with the new emails.
     *
     * @param userId           User's unique ID
     * @param primaryEmail     User's primary email
     * @param additionalEmails User's updated emails
     * @return returns the User with the updated email
     */
    public User update(Long userId, String primaryEmail, List<String> additionalEmails) {
        final List<UserEmail> existingEmails;
        final List<UserEmail> updatedEmails = new ArrayList<>();
        final List<String> allNewEmails = new ArrayList<>(additionalEmails);
        allNewEmails.add(primaryEmail);

        if (allNewEmails.size() > MAX_EMAIL_COUNT) {
            throw new MaxEmailCountReachedException();
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        // Existing emails will be used to track which emails are no longer used
        existingEmails = new ArrayList<>(user.getUserEmails());

        // Assert that all given emails are not taken by another account
        for (String address : allNewEmails) {
            UserEmail userEmail = userEmailRepository.getEmailFromAddress(address);
            if (userEmail != null) {
                if (userEmail.getUser() != user) {
                    throw new EmailRegisteredWithAnotherUserException(address);
                } else {
                    // mark email as not having to be deleted after
                    existingEmails.remove(userEmail);
                    // Setting this here is fine as the "transaction" only completes upon saving the user
                    userEmail.setIsPrimary(address.equals(primaryEmail));
                }
            } else {
                userEmail = new UserEmail(address, user, address.equals(primaryEmail));
            }
            updatedEmails.add(userEmail);
        }

        user.setUserEmails(updatedEmails);
        // Remove all emails no longer associated with this account
        userEmailRepository.deleteAll(existingEmails);

        return user;
    }

    public List<String> read() {
        return userEmailRepository.getAllEmails();
    }

    /**
     * Checks and creates the new email that will be associated to a user
     *
     * @param user    The user which the email will belong to
     * @param email   The email that will belong to a user
     * @param primary Boolean that determines if email is a primary email or not
     * @return returns a newly created user email
     */
    public UserEmail verifyAndCreateUserEmail(User user, String email, boolean primary) {
        UserEmail emailFromAddress = userEmailRepository.getEmailFromAddress(email);

        if (emailFromAddress == null) {
            return new UserEmail(email, user, primary);
        }

        throw new EmailAlreadyRegisteredException(email);
    }

    /**
     * Checks and create every email that will be associated to a user
     *
     * @param user   The user which the emails will belong to
     * @param emails The list of potential user emails
     * @return List of newly created user emails
     */
    public List<UserEmail> verifyAndCreateUserEmail(User user, List<String> emails) {
        return emails.stream()
                .map(e -> verifyAndCreateUserEmail(user, e, false))
                .collect(Collectors.toList());
    }

    /**
     * Checks whether the given email is already in use
     *
     * @param email The email address to check whether is already in use by a user
     * @return Boolean for if the email is in use
     */
    public boolean getEmailIsInUse(String email) {
        UserEmail emailFromAddress = userEmailRepository.getEmailFromAddress(email);
        return emailFromAddress != null;
    }
}

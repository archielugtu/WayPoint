package com.springvuegradle.team200.service;

import com.springvuegradle.team200.dto.request.*;
import com.springvuegradle.team200.dto.response.SearchUserResponse;
import com.springvuegradle.team200.dto.response.UserAdminStatusResponse;
import com.springvuegradle.team200.exception.InvalidPasswordException;
import com.springvuegradle.team200.exception.UserNotFoundException;
import com.springvuegradle.team200.model.*;
import com.springvuegradle.team200.repository.ActivityRepository;
import com.springvuegradle.team200.repository.UserRepository;
import com.springvuegradle.team200.repository.specifications.UserSpecs;
import org.hibernate.Hibernate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class handles CRUD logic associated with user's data
 */

@Service
public class UserService {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserActivityService userActivityService;

    @Autowired
    UserPassportService userPassportService;

    @Autowired
    UserEmailService userEmailService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ActivityRepository activityRepository;

    /**
     * Creates a new user and saves it to the database
     *
     * @param registerRequest Request containing user data
     * @return User object created
     */
    @Transactional
    public User create(RegisterRequest registerRequest) {
        List<String> activities = registerRequest.getActivities();
        String emailAddress = registerRequest.getEmailAddress();
        List<String> additionalEmails = registerRequest.getAdditionalEmails();
        List<String> passports = registerRequest.getPassports();

        User newUser = new User();
        // Copy properties from register request to user obj,
        // location is ignored since it needs to be saved manually first
        BeanUtils.copyProperties(registerRequest, newUser);

        // Password
        String hashedPassword = passwordEncoder.encode(registerRequest.getPassword());
        newUser.setPassword(hashedPassword);

        // Activity Types
        List<ActivityType> activityTypes = userActivityService
                .verifyAndRetrieveActivityTypes(activities);
        newUser.setActivityTypes(activityTypes);

        // Passport
        List<PassportCountry> passportCountries = userPassportService
                .verifyAndRetrievePassportByCountry(passports);
        newUser.setPassports(passportCountries);

        // Birth date
        newUser.setBirthDate(new SimpleDateFormat("yyyy-MM-dd").format(registerRequest.getBirthDate()));

        // Emails
        UserEmail primaryEmail = userEmailService
                .verifyAndCreateUserEmail(newUser, emailAddress, true);
        List<UserEmail> secondaryEmails = userEmailService
                .verifyAndCreateUserEmail(newUser, additionalEmails);

        List<UserEmail> associatedEmails = new ArrayList<>();
        associatedEmails.add(primaryEmail);
        associatedEmails.addAll(secondaryEmails);
        newUser.setUserEmails(associatedEmails);

        userRepository.save(newUser);
        return newUser;
    }

    /**
     * Retrieves a single user with specified ID from database
     *
     * @param userId User's unique ID
     * @return User object found
     */
    @Transactional
    public User read(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        // Load lazily fetched data
        Hibernate.initialize(user.getActivityTypes());
        Hibernate.initialize(user.getUserEmails());
        Hibernate.initialize(user.getActivityTypes());
        Hibernate.initialize(user.getPassports());

        if (user.getIsGlobalAdmin()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return user;
    }

    /**
     * Retrieves list of users' admin status and their userId that matches emails
     * given
     *
     * @param emails List of user's emails
     * @return List of users' ID, admin status, and primary email
     */
    @Transactional
    public List<UserAdminStatusResponse> readAdminStatusByEmail(List<String> emails) {
        if (emails == null || emails.isEmpty()) return List.of();

        return userRepository.findAll(UserSpecs.emailAddressIn(emails))
                .stream()
                .map(UserAdminStatusResponse::of)
                .collect(Collectors.toList());
    }

    /**
     * Updates a user's profile data in the database
     *
     * @param userId             User's unique ID
     * @param editProfileRequest Request containing user's updated data
     */
    @Transactional
    public void update(Long userId, EditProfileRequest editProfileRequest) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        // Copy properties from register request to user obj,
        // location is ignored since it needs to be saved manually first
        BeanUtils.copyProperties(editProfileRequest, user);

        user.setUsername(editProfileRequest.getNickname());

        String birthDate = new SimpleDateFormat("yyyy-MM-dd").format(editProfileRequest.getBirthDate());
        user.setBirthDate(birthDate);

        // Emails
        String primaryEmail = editProfileRequest.getPrimaryEmail();
        List<String> additionalEmails = editProfileRequest.getAdditionalEmails();
        userEmailService.update(userId, primaryEmail, additionalEmails);

        // Passports
        List<String> passports = editProfileRequest.getPassports();
        userPassportService.update(userId, passports);

        // Location
        Location location = editProfileRequest.getLocation();
        user.setLocation(location);

        userRepository.save(user);
    }

    /**
     * Update a user's email in the database
     *
     * @param userId           User's unique ID
     * @param editEmailRequest Request containing user's updated email
     */
    @Transactional
    public void update(Long userId, EditEmailRequest editEmailRequest) {
        User user;
        String primaryEmail = editEmailRequest.getPrimaryEmail();
        List<String> secondaryEmails = editEmailRequest.getAdditionalEmails();
        user = userEmailService.update(userId, primaryEmail, secondaryEmails);

        userRepository.save(user);
    }

    /**
     * Update a user's password in the database
     *
     * @param userId          User's unique ID
     * @param passwordRequest Request containing user's updated password
     */
    @Transactional
    public void update(Long userId, PasswordRequest passwordRequest, boolean isRequestByAdmin) {
        String password = passwordRequest.getPassword();
        String newPassword = passwordRequest.getNewPassword();
        String confirmPassword = passwordRequest.getConfirmPassword();

        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        if (!isRequestByAdmin && !passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidPasswordException();
        }

        if (!newPassword.equals(confirmPassword)) {
            throw new InvalidPasswordException();
        }

        String hashedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(hashedPassword);

        userRepository.save(user);
    }

    /**
     * Retrieves paginated users from the database based on query
     * <p>
     * the @Transactional annotation is required to fetch lazily loaded fields
     *
     * @param size              number of items to be returned in a single page
     * @param searchUserRequest request object containing search parameters
     * @return Paginated SearchUserResponse object
     */
    @Transactional
    public Page<SearchUserResponse> readByQuery(int size, SearchUserRequest searchUserRequest, User searchingUser) {
        if (isEmptySearch(searchUserRequest)) {
            return Page.empty();
        }
        Specification<User> specification = generateDefaultSpecification(searchUserRequest, searchingUser.getId());
        specification = generateActivityTypeSpecification(searchUserRequest, specification);

        return userRepository.findAll(
                specification,
                PageRequest.of(
                        searchUserRequest.getPage(),
                        size,
                        // compare with ID if first name is not sufficient
                        Sort.by(Sort.Direction.ASC, "firstName", "id"))
        ).map(SearchUserResponse::of);
    }

    private Specification<User> generateDefaultSpecification(SearchUserRequest searchUserRequest,
                                                             Long searchingUserId) {
        return Specification.where(
                UserSpecs.byFirstName(searchUserRequest.getFirstname())
        ).and(
                UserSpecs.byLastName(searchUserRequest.getLastname())
        ).and(
                UserSpecs.byEmail(searchUserRequest.getEmail())
        ).and(
                UserSpecs.isNotGlobalAdmin()
        ).and(
                UserSpecs.userIdIsNot(searchingUserId)
        );
    }

    private Specification<User> generateActivityTypeSpecification(SearchUserRequest searchUserRequest,
                                                                  Specification<User> defaultSpec) {
        String activities = searchUserRequest.getActivities();
        if (activities == null || activities.isBlank()) {
            return defaultSpec;
        }

        String[] activitiesArr = searchUserRequest.getActivities().split("%20");
        List<String> activityTypes = new ArrayList<>(List.of(activitiesArr));

        List<Long> activityTypesIds = userActivityService.verifyAndRetrieveActivityTypes(activityTypes)
                .stream()
                .map(ActivityType::getActivityTypeId)
                .collect(Collectors.toList());

        if (searchUserRequest.getMethod().equals("and")) {
            for (Long activityTypesId : activityTypesIds) {
                defaultSpec = defaultSpec.and(UserSpecs.hasActivityTypeOfId(activityTypesId));
            }
        } else {
            defaultSpec = defaultSpec.and(UserSpecs.hasAnyActivityTypeOfIds(activityTypesIds));
        }

        return defaultSpec;
    }

    /**
     * Update a user's role in the database
     *
     * @param userId      User's unique ID
     * @param roleRequest Request containing user's updated role
     */
    public void update(Long userId, RoleRequest roleRequest) {
        String role = roleRequest.getRole();
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        if (role.equals("admin")) {
            user.setIsAdmin(true);
        } else if (role.equals("user")) {
            user.setIsAdmin(false);
        }

        userRepository.save(user);

    }

    /**
     * Delete a user in the database
     *
     * @param userId User's unique ID
     */
    @Transactional
    public void delete(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        // Keep activities around
        for (var activity : user.getActivityCreator()) {
            activity.setCreator(null);
            activityRepository.save(activity);
        }
        user.setActivityCreator(Set.of());
        user.setActivitiesFollowing(Set.of());
        user.setActivityParticipator(Set.of());
        user.setActivityOrganiser(Set.of());

        userRepository.delete(user);
    }


    /**
     * Checks if search request is empty
     *
     * @param searchUserRequest The search request
     * @return a boolean that determines if request is empty
     */
    private boolean isEmptySearch(SearchUserRequest searchUserRequest) {
        return(searchUserRequest.getFirstname().isBlank()
                && searchUserRequest.getLastname().isBlank()
                && searchUserRequest.getEmail().isBlank()
                && searchUserRequest.getActivities().isBlank());
    }
}
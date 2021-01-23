package com.springvuegradle.team200.service;

import com.springvuegradle.team200.dto.request.*;
import com.springvuegradle.team200.dto.response.*;
import com.springvuegradle.team200.exception.*;
import com.springvuegradle.team200.model.*;
import com.springvuegradle.team200.repository.*;
import com.springvuegradle.team200.repository.specifications.ActivitySpec;
import org.hibernate.Hibernate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class handles CRUD logic associated with users' activities
 */
@Service
public class UserActivityService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserActivityHistoryRepository userActivityHistoryRepository;

    @Autowired
    ActivityRepository activityRepository;

    @Autowired
    ActivityTypesRepository activityTypesRepository;

    @Autowired
    HashtagRepository hashtagRepository;

    @Autowired
    OutcomeQuestionRepository outcomeQuestionRepository;

    @Autowired
    OutcomeMultichoiceOptionRepository outcomeMultichoiceOptionRepository;

    @Autowired
    OutcomeUserAnswerRepository userAnswerRepository;

    @Autowired
    ActivityRolesService activityRolesService;

    /**
     * Creates a new activity associated with a user
     * User is automatically set as participant and creator
     *
     * @param userId          User's unique ID
     * @param activityRequest Activity Request DTO
     */
    @Transactional
    public Activity create(Long userId, ActivityRequest activityRequest) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        verifyActivityDate(
                activityRequest.isContinuous(),
                activityRequest.getStartDate(),
                activityRequest.getEndDate()
        );

        final List<ActivityType> activityTypes = verifyAndRetrieveActivityTypes(activityRequest.getActivityTypes());
        final Set<Hashtag> hashtags = verifyAndRetrieveHashtags(activityRequest.getHashtags());
        Date creationDate = new Date();

        Activity activity = new Activity();
        BeanUtils.copyProperties(activityRequest, activity);
        activity.setActivityTypes(activityTypes);
        activity.setCreationDate(creationDate);
        activity.setHashtags(hashtags);
        activity.setCreator(user);
        Set<OutcomeQuestion> specifications = verifyAndRetrieveResults(activityRequest.getOutcomeQuestions(), activity);
        activity.setOutcomeQuestions(specifications);

        return activityRepository.save(activity);
    }

    /**
     * Retrieves all activities associated with a user
     *
     * @param userId User's unique ID
     * @return List of ActivityResponse DTO
     */
    @Transactional
    public List<ActivityResponse> readByUserId(Long userId) {
        userRepository
                .findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        List<Activity> activityList = activityRepository.findAllActivitiesByUserId(userId);

        return getActivityResponses(userId, activityList);
    }

    @Transactional
    public List<ActivityResultSpecResponse> readActivitySpec(Long activityId) {
        Activity activity = activityRepository.findById(activityId).orElseThrow(() -> {
            throw new ActivityNotFoundException(activityId);
        });
        Set<OutcomeQuestion> outcomeQuestions = activity.getOutcomeQuestions();
        List<ActivityResultSpecResponse> response = new ArrayList<>();
        for (OutcomeQuestion outcomeQuestion : outcomeQuestions) {
            ActivityResultSpecResponse dto = ActivityResultSpecResponse.of(outcomeQuestion);
            boolean answered = userAnswerRepository.countOutcomeUserAnswerByOutcomeQuestion(outcomeQuestion) > 0;
            dto.setAnswered(answered);
            response.add(dto);
        }
        return response;
    }

    /**
     * Retrieves all activities created by the user
     *
     * @param userId User's unique ID
     * @return List of ActivityResponse DTO
     */
    @Transactional
    public List<ActivityResponse> readCreatedActivitiesByUserId(Long userId) {
        userRepository
                .findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        List<Activity> activityList = activityRepository.findActivitiesCreatedByUserId(userId);
        return getActivityResponses(userId, activityList);
    }


    private List<ActivityResponse> getActivityResponses(Long userId, List<Activity> activityList) {
        List<ActivityResponse> responses = new ArrayList<>();
        for (Activity activity : activityList) {
            Long activityId = activity.getId();
            int activityRole = activityRepository.findActivityRole(userId, activityId);
            responses.add(new ActivityResponse(activity, activityRole));
        }
        return responses;
    }

    /**
     * Retrieves activity information by its unique ID
     *
     * @param activityId Activity's unique ID
     * @return Activity information
     */
    @Transactional
    public Activity readByActivityId(Long activityId) {
        Activity activity = activityRepository
                .findById(activityId)
                .orElseThrow(() -> new ActivityNotFoundException(activityId));


        Hibernate.initialize(activity.getActivityTypes());
        Hibernate.initialize(activity.getOrganisers());
        Hibernate.initialize(activity.getParticipants());
        Hibernate.initialize(activity.getOutcomeQuestions());
        Hibernate.initialize(activity.getFollowers());
        Hibernate.initialize(activity.getHashtags());
        return activity;
    }

    /**
     * Retrieves all activity types a user is interested in
     *
     * @param userId User's unique ID
     * @return List of activity types user is interested in
     */
    @Transactional
    public List<String> readActivityTypes(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        return user.getActivityTypes()
                .stream()
                .map(ActivityType::getType)
                .collect(Collectors.toList());
    }

    /**
     * Returns all the available activity types from the database
     *
     * @return List of activity type names
     */
    @Transactional
    public List<String> readAll() {
        return activityTypesRepository.getAllActivityTypes();
    }

    /**
     * Modifies existing activity details
     * Only accessible by activity's creator or admin
     *
     * @param activityId      Activity's unique ID
     * @param userId          User's unique ID
     * @param activityRequest Activity Request DTO
     */
    @Transactional
    public Activity edit(Long activityId, Long userId, ActivityRequest activityRequest) {

        Activity activity = activityRepository
                .findById(activityId)
                .orElseThrow(() -> new ActivityNotFoundException(activityId));

        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (!activity.userHasEditPrivilege(user)) {
            throw new InsufficientPermissionException();
        }

        verifyActivityDate(
                activityRequest.isContinuous(),
                activityRequest.getStartDate(),
                activityRequest.getEndDate()
        );

        BeanUtils.copyProperties(activityRequest, activity);
        final Set<Hashtag> hashtags = verifyAndRetrieveHashtags(activityRequest.getHashtags());
        activity.setHashtags(hashtags);

        final Location location = activityRequest.getLocation();
        activity.setLocation(location);

        List<ActivityType> activityTypes = verifyAndRetrieveActivityTypes(activityRequest.getActivityTypes());
        activity.setActivityTypes(activityTypes);

        List<OutcomeQuestion> byActivity = outcomeQuestionRepository.findByActivity(activity);
        for (OutcomeQuestion outcomeQuestion : byActivity) {
            outcomeQuestion.setActivity(null);
        }
        outcomeQuestionRepository.saveAll(byActivity);

        Set<OutcomeQuestion> specifications = verifyAndRetrieveResults(activityRequest.getOutcomeQuestions(), activity);
        activity.setOutcomeQuestions(specifications);

        activity.setVisibility(activityRequest.getVisibility());

        return activityRepository.save(activity);
    }

    /**
     * Removes a user's activity with specified ID from the database
     * Only accessible by activity's creator or admin
     *
     * @param userId     User's unique ID
     * @param activityId Activity's unique ID
     */
    @Transactional
    public void delete(Long userId, Long activityId) {
        Activity activity = activityRepository
                .findById(activityId)
                .orElseThrow(() -> new ActivityNotFoundException(activityId));

        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // Checks if user has delete activity privileges
        if (!activity.userHasDeletePrivilege(user)) {
            throw new InsufficientPermissionException();
        }

        List<OutcomeQuestion> byActivity = outcomeQuestionRepository.findByActivity(activity);
        for (OutcomeQuestion outcomeQuestion : byActivity) {
            userAnswerRepository.deleteAllByOutcomeQuestion(outcomeQuestion);
        }

        // Delete activity from user's relations
        user.removeActivity(activity);
        userRepository.save(user);
        activityRepository.delete(activity);
    }

    @Transactional
    public Set<OutcomeQuestion> verifyAndRetrieveResults(Set<OutcomeQuestion> specs, Activity activity) {
        // If it exists in the database, rename the old one
        Set<OutcomeQuestion> savedToDB = new HashSet<>();
        if (specs == null || specs.isEmpty()) {
            return savedToDB;
        }

        // for each questions we receive
        for (OutcomeQuestion spec : specs) {
            if (spec == null) {
                continue;
            }
            OutcomeQuestion outcomeQuestion;
            // if they already contain ID
            if (spec.getId() != null) {
                // check if it's already in the database
                Long specId = spec.getId();
                outcomeQuestion = outcomeQuestionRepository
                        .findById(spec.getId())
                        .orElseThrow(() -> new InvalidOutcomeQuestionException(specId));
                // update its properties
                outcomeMultichoiceOptionRepository.deleteByOutcomeQuestion_Id(specId);
                BeanUtils.copyProperties(spec, outcomeQuestion);
                for (OutcomeMultichoiceOption answer : spec.getActivityResultPossibleAnswers()) {
                    answer.setOutcomeQuestion(outcomeQuestion);
                }
            }
            // does not contain ID, meaning it's new questions
            outcomeQuestion = spec;
            if (!spec.getActivityResultPossibleAnswers().isEmpty()) {
                for (OutcomeMultichoiceOption answer : spec.getActivityResultPossibleAnswers()) {
                    answer.setOutcomeQuestion(outcomeQuestion);
                }
            }
            // save
            outcomeQuestion.setActivity(activity);
            outcomeQuestionRepository.save(outcomeQuestion);
            savedToDB.add(outcomeQuestion);
        }
        return savedToDB;
    }

    /**
     * Verifies a List of activity type names are indeed stored in the database
     * and retrieves it from the database
     *
     * @param activityTypes List of activity type names
     * @return List of Activity Type objs from the database
     */
    @Transactional
    public List<ActivityType> verifyAndRetrieveActivityTypes(List<String> activityTypes) {
        if (activityTypes == null) return new ArrayList<>();

        // Converts the string of activity types into list of ActivityType object.
        final List<ActivityType> finalActivities = new ArrayList<>();
        for (String activityType : activityTypes) {
            ActivityType foundActivityType = activityTypesRepository
                    .findByType(activityType)
                    .orElseThrow(() -> new ActivityNotFoundException(activityType));

            finalActivities.add(foundActivityType);
        }

        return finalActivities;
    }

    /**
     * Verifies the list of hashtags and then returns a set of hashtag objects
     *
     * @param hashtags set of hashtag strings
     * @return Set of hashtag objects from strings
     */
    @Transactional
    public Set<Hashtag> verifyAndRetrieveHashtags(List<String> hashtags) {
        if (hashtags == null) return new HashSet<>();

        // Converts the string of hashtags into list of Hashtag object.
        final Set<Hashtag> finalHashtags = new HashSet<>();
        for (String hashtag : hashtags) {
            Optional<Hashtag> foundHashtag = hashtagRepository.findFirstByName(hashtag);
            foundHashtag.ifPresentOrElse(finalHashtags::add, () -> {
                Hashtag newHashtag = new Hashtag(hashtag);
                hashtagRepository.save(newHashtag);
                finalHashtags.add(newHashtag);
            });
        }

        return finalHashtags;
    }

    /**
     * Checks if start and end date of an activity is valid
     * - Start date should not be after end date
     * - Start date should not be in the past
     * - End date should not be in the past
     *
     * @param isContinuous True if activity does not have an start/end date, False otherwise
     * @param startDate    Start date
     * @param endDate      End date
     */
    private void verifyActivityDate(boolean isContinuous, Date startDate, Date endDate) {
        if (!isContinuous) {
            LocalDate end = endDate.toInstant().atZone(ZoneOffset.UTC).toLocalDate();
            LocalDate start = startDate.toInstant().atZone(ZoneOffset.UTC).toLocalDate();
            LocalDate today = LocalDate.now(ZoneOffset.UTC);

            if (end.isBefore(start)
                    || end.isBefore(today)
                    || start.isBefore(today)) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Cannot have end date or start date before the today's date."
                );
            }
        }
    }

    /**
     * Modifies activity types that a user is interested in
     *
     * @param activityRequest Request object containing activity types
     * @param userId          User's unique ID
     */
    @Transactional
    public void editUserActivityTypes(ActivityTypeRequest activityRequest, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        final List<String> activities = activityRequest.getActivities();
        final List<ActivityType> finalActivities = new ArrayList<>();
        for (String activity : activities) {
            ActivityType foundActivityType = activityTypesRepository
                    .findByType(activity)
                    .orElseThrow(() -> new ActivityNotFoundException(activity));

            finalActivities.add(foundActivityType);
        }

        user.setActivityTypes(finalActivities);
        userRepository.save(user);
    }

    /**
     * Gets the user's role for the given activity
     *
     * @param userId     The unique ID of the user to have their role edited
     * @param activityId The unique ID of the activity for which the user would like their role changed
     */
    @Transactional
    public ActivityRole getUserRole(Long userId, Long activityId) {
        int roleId = activityRepository.findActivityRole(userId, activityId);
        return ActivityRole.values()[roleId];
    }

    /**
     * Modifies the user's role for the given activity. Does not permit changing to creator
     *
     * @param userId                  The unique ID of the user to have their role edited
     * @param activityId              The unique ID of the activity for which the user would like their role changed
     * @param editActivityRoleRequest Request object containing the user's new role
     */
    @Transactional
    public void editUserRole(Long userId, Long activityId, EditActivityRoleRequest editActivityRoleRequest) {
        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Activity activity = activityRepository
                .findById(activityId)
                .orElseThrow(() -> new ActivityNotFoundException(activityId));

        // If someone is trying to modify a creator role, throw Forbidden exception
        if (ActivityRole.CREATOR.ordinal() == activityRepository.findActivityRole(userId, activityId)) {
            throw new ChangeCreatorRoleNotPermittedException(activityId);
        }

        switch (editActivityRoleRequest.getRole()) {
            case FOLLOWER:
                user.addActivityFollowing(activity);
                break;

            case PARTICIPANT:
                user.addActivityParticipation(activity);
                break;

            case ORGANISER:
                user.addActivityOrganising(activity);
                break;

            default:
                break;
        }
        userRepository.save(user);
    }

    /**
     * Deletes the user's role for the given activity
     *
     * @param userId     The unique ID of the user to have their role deleted
     * @param activityId The unique ID of the activity for which the user would like their role deleted
     */
    @Transactional
    public void deleteUserRole(Long userId, Long activityId) {
        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Activity activity = activityRepository
                .findById(activityId)
                .orElseThrow(() -> new ActivityNotFoundException(activityId));

        user.deleteRole(activity);
        userRepository.save(user);
    }

    /**
     * Retrieves user following status for an activity
     *
     * @param userId     User's unique ID
     * @param activityId Activity's unique ID
     * @return User's activity following status response
     */
    @Transactional
    public ActivityFollowingStatusResponse retrieveUserActivityFollowingStatus(Long userId, Long activityId) {

        boolean followingStatus = false;

        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Set<Activity> activitiesFollowing = user.getActivitiesFollowing();

        for (Activity activity : activitiesFollowing) {
            if (activity.getId().equals(activityId)) {
                followingStatus = true;
                break;
            }
        }

        return new ActivityFollowingStatusResponse(followingStatus);
    }

    /**
     * Retrieves paginated activities from the database based on query
     * <p>
     * the @Transactional annotation is required to fetch lazily loaded fields
     *
     * @param request request object containing search parameters
     * @return Paginated SearchActivityResponse object
     */
    @Transactional
    public Page<SearchActivityResponse> readByQuery(SearchActivityRequest request, Long callerId) {
        User callerUser = userRepository
                .findById(callerId)
                .orElseThrow(() -> new UserNotFoundException(callerId));

        Specification<Activity> spec = Specification.where(null);
        Specification<Activity> hashtagSpecification = generateHashTagSpecification(request, callerUser);
        Specification<Activity> activityTypeSpecification = generateActivityTypeSpecification(request);
        Specification<Activity> activityNameSpecification = generateActivityNameSpecification(request);
        Specification<Activity> activityDateSpecification = generateActivityDateSpecification(request);
        Specification<Activity> activityLocationSpecification = generateActivityLocationSpecification(request);

        spec = spec.and(hashtagSpecification);
        spec = spec.and(activityTypeSpecification);
        spec = spec.and(activityNameSpecification);
        spec = spec.and(activityLocationSpecification);
        spec = spec.and(activityDateSpecification);

        if (!callerUser.getIsAdmin()) {
            Specification<Activity> publicOrSharedOrOwner = Specification.where(ActivitySpec.hasPublicVisibility());
            publicOrSharedOrOwner = publicOrSharedOrOwner.or(ActivitySpec.userIsShared(callerUser));
            publicOrSharedOrOwner = publicOrSharedOrOwner.or(ActivitySpec.userIsOwner(callerUser.getId()));
            spec = spec.and(publicOrSharedOrOwner);
        }

        String resultSortField = "startDate";
        if ("name".equals(request.getSort())) {
            resultSortField = "activityName";
        }

        return activityRepository.findAll(
                spec,
                PageRequest.of(
                        request.getP(),
                        request.getSize(),
                        Sort.by(Sort.Direction.ASC, resultSortField, "id"))
        ).map(SearchActivityResponse::of);
    }

    /**
     * Generates the activities specification based on the searchActivityRequest
     *
     * @param searchActivityRequest the request containing the activity date
     * @return the Specification object
     */
    private Specification<Activity> generateActivityDateSpecification(SearchActivityRequest searchActivityRequest) {
        if (null == searchActivityRequest.getDate_end() || null == searchActivityRequest.getDate_start()) {
            return Specification.where(null);
        }

        return Specification.where(ActivitySpec.activityInDates(
                searchActivityRequest.getDate_start(),
                searchActivityRequest.getDate_end()
        ));
    }

    /**
     * Generates the activities specification based on the searchActivityRequest
     *
     * @param searchActivityRequest the request containing the activity location
     * @return the Specification object
     */
    private Specification<Activity> generateActivityLocationSpecification(SearchActivityRequest searchActivityRequest) {
        Specification<Activity> defaultSpecification = Specification.where(null);
        if (searchActivityRequest.isViewportRestricted()) {
            defaultSpecification = Specification.where(
                    ActivitySpec.isActivityWithinBounds(
                            searchActivityRequest.getSw_lat(),
                            searchActivityRequest.getSw_lng(),
                            searchActivityRequest.getNe_lat(),
                            searchActivityRequest.getNe_lng()
                    )
            );
        } else {
            defaultSpecification = Specification.where(null);
        }

        return defaultSpecification;
    }

    /**
     * Generates the activities specification based on the searchActivityRequest
     *
     * @param searchActivityRequest the request containing the hashtags and method
     * @return the Specification object
     */
    private Specification<Activity> generateHashTagSpecification(SearchActivityRequest searchActivityRequest, User user) {
        Specification<Activity> hashtagSpecification = Specification.where(null);
        String hashtags = searchActivityRequest.getHashtag();
        if (hashtags.length() == 0) {
            return hashtagSpecification;
        }

        List<Long> hashTagIds = verifyAndRetrieveHashtags(Arrays.asList(hashtags.split(";")))
                .stream()
                .map(Hashtag::getHashtagId)
                .collect(Collectors.toList());
        if (searchActivityRequest.getHashtagSearchType().equals("and")) {
            for (Long hashTagId : hashTagIds) {
                hashtagSpecification = hashtagSpecification.and(ActivitySpec.hasHashTagId(hashTagId));
            }
        } else {
            hashtagSpecification = Specification.where(ActivitySpec.hasAnyHashTagId(hashTagIds));
        }
        return hashtagSpecification;
    }

    /**
     * Generates the activities specification based on the searchActivityRequest
     *
     * @param searchActivityRequest the request containing the activity name
     * @return the Specification object
     */
    private Specification<Activity> generateActivityNameSpecification(SearchActivityRequest searchActivityRequest) {
        if (searchActivityRequest.getName().equals("")) {
            return null;
        }
        Specification<Activity> defaultSpecification = Specification.where(ActivitySpec.hasActivityName(
                searchActivityRequest.getName()
        ));
        return defaultSpecification;
    }

    /**
     * Generates the activities specification based on the SearchActivityRequest
     *
     * @param request the request containing the activity types and method
     * @return the Specification object
     */
    private Specification<Activity> generateActivityTypeSpecification(SearchActivityRequest request) {
        Specification<Activity> activitySpecification = Specification.where(null);
        String activityTypes = request.getActivityType();
        if (activityTypes.length() == 0) {
            return activitySpecification;
        }

        List<Long> activityTypeIDs = verifyAndRetrieveActivityTypes(Arrays.asList(activityTypes.split(";")))
                .stream()
                .map(ActivityType::getActivityTypeId)
                .collect(Collectors.toList());

        if (request.getActivityTypeSearchType().equals("and")) {
            for (Long activityTypeID : activityTypeIDs) {
                activitySpecification = activitySpecification.and(ActivitySpec.hasActivityTypeId(activityTypeID));
            }
        } else {
            activitySpecification = Specification.where(ActivitySpec.hasAnyActivityTypeId(activityTypeIDs));
        }
        return activitySpecification;
    }

    /**
     * Retrieves paginated users from the database based who are followers for a specified activity
     * <p>
     * the @Transactional annotation is required to fetch lazily loaded fields
     *
     * @param size       number of items to be returned in a single page
     * @param activityId the id of the activity that is being used in the query
     * @return Paginated CompactUserResponse object
     */
    @Transactional
    public Page<CompactUserResponse> readByFollowers(int size, Long activityId, int page) {
        Activity activity = activityRepository
                .findById(activityId)
                .orElseThrow(() -> new ActivityNotFoundException(activityId));
        Set<User> followers = activity.getFollowers();
        if (followers.isEmpty()) {
            return Page.empty();
        }
        return userRepository.findFollowersByActivityId(activityId, PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.ASC, "first_name", "last_name"))).map(CompactUserResponse::of);
    }

    /**
     * Retrieves paginated users from the database based who are organisers for a specified activity
     * <p>
     * the @Transactional annotation is required to fetch lazily loaded fields
     *
     * @param size       number of items to be returned in a single page
     * @param activityId the id of the activity that is being used in the query
     * @return Paginated CompactUserResponse object
     */
    @Transactional
    public Page<CompactUserResponse> readByOrganisers(int size, Long activityId, int page) {
        Activity activity = activityRepository
                .findById(activityId)
                .orElseThrow(() -> new ActivityNotFoundException(activityId));
        Set<User> organisers = activity.getOrganisers();
        if (organisers.isEmpty()) {
            return Page.empty();
        }
        return userRepository.findOrganisersByActivityId(activityId, PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.ASC, "first_name", "last_name"))).map(CompactUserResponse::of);
    }

    /**
     * Retrieves paginated users from the database based who are participants for a specified activity
     * <p>
     * the @Transactional annotation is required to fetch lazily loaded fields
     *
     * @param size       number of items to be returned in a single page
     * @param activityId the id of the activity that is being used in the query
     * @return Paginated CompactUserResponse object
     */
    @Transactional
    public Page<CompactUserResponse> readByParticipants(int size, Long activityId, int page) {
        Activity activity = activityRepository
                .findById(activityId)
                .orElseThrow(() -> new ActivityNotFoundException(activityId));
        Set<User> participants = activity.getParticipants();
        if (participants.isEmpty()) {
            return Page.empty();
        }
        return userRepository.findParticipantsByActivityId(activityId, PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.ASC, "first_name", "last_name"))).map(CompactUserResponse::of);
    }

    /**
     * Modifies the activity visibility type and add accessors if it's set to restricted
     *
     * @param activityId                Activity's unique ID
     * @param activityVisibilityRequest The request containing the activity visibility and accessors
     *                                  (if set to restricted)
     */
    @Transactional
    public void editActivityVisibility(Long activityId, ActivityVisibilityRequest activityVisibilityRequest) {
        Activity activity = activityRepository
                .findById(activityId)
                .orElseThrow(() -> new ActivityNotFoundException(activityId));
        ActivityVisibility visibility = activityVisibilityRequest.getVisibility();
        switch (visibility) {
            case PRIVATE:
                removeAllNonCreatorUsersFromActivity(activity);
                break;
            case PUBLIC:
            case RESTRICTED:
                setAllActivityUsers(activity, activityVisibilityRequest.getAccessors());
                break;
            default:
                break;
        }

        activity.setVisibility(visibility);
        activityRepository.save(activity);
    }

    /**
     * Removes all users form an activity except for the creator
     *
     * @param activity activity to have non creators removed from
     */
    private void removeAllNonCreatorUsersFromActivity(Activity activity) {
        Long activityId = activity.getId();
        activityRepository.removeFollowersOfActivityId(activityId);
        activityRepository.removeOrganisersOfActivityId(activityId);
        activityRepository.removeParticipantsOfActivityId(activityId);
        activityRepository.removeSharedUsersOfActivity(activityId);
    }

    /**
     * Removes all users from activity then adds back the ones specified in the request
     *
     * @param activity activity to have users edited
     * @param requests request containing list of new users and their roles
     */
    private void setAllActivityUsers(Activity activity, List<ActivityAccessorRequest> requests) {
        Long activityId = activity.getId();
        activity.clearSharedUsers();
        activityRepository.removeParticipantsOfActivityId(activityId);
        activityRepository.removeFollowersOfActivityId(activityId);
        activityRepository.removeOrganisersOfActivityId(activityId);

        for (ActivityAccessorRequest request : requests) {
            User byEmail = userRepository.getUserFromEmail(request.getEmail());
            if (byEmail == null) {
                continue;
            }
            EditActivityRoleRequest editRoleRequest = new EditActivityRoleRequest();
            editRoleRequest.setRole(request.getRole());
            editUserRole(byEmail.getId(), activityId, editRoleRequest);
            activity.addSharedUser(byEmail);
        }
        activityRepository.save(activity);
    }

    /**
     * Returns the count of activity's organisers, participants, and followers
     *
     * @param activityId Activity's unique ID
     * @return Count of activity's organisers, participants, followers
     */
    @Transactional
    public ActivityUsersCountResponse countUsersByActivityId(Long activityId) {
        activityRepository.findById(activityId)
                .orElseThrow(() -> new ActivityNotFoundException(activityId));

        ActivityUsersCountResponse response = new ActivityUsersCountResponse();
        response.setTotalFollowers(activityRepository.countFollowersByActivityId(activityId));
        response.setTotalOrganisers(activityRepository.countOrganisersByActivityId(activityId));
        response.setTotalParticipants(activityRepository.countParticipantsByActivityId(activityId));
        response.setVisibility(activityRepository.findActivityVisibilityById(activityId));
        return response;
    }

    /**
     * Returns paginated result of all users related to an activity
     *
     * @param activityId Activity's unique ID
     * @param page       page number - starts from 0
     * @param size       total items in a single page
     * @return Paginated result of all users related to an activity
     */
    @Transactional
    public Page<UserRoleDTO> readAllUsersRelatedToActivity(Long activityId, int page, int size) {
        activityRepository.findById(activityId)
                .orElseThrow(() -> new ActivityNotFoundException(activityId));

        Page<Map<String, Object>> results = userRepository
                .findUsersRelatedToActivityByActivityId(activityId, PageRequest.of(page, size, Sort.by("userId")));

        return results.map(UserRoleDTO::of);
    }


    /**
     * Returns the count of activity's organisers, participants, and followers
     *
     * @param activityId Activity's unique ID
     * @return Count of activity's organisers, participants, followers
     */
    @Transactional
    public Set<UserResponse> getActivityOutcomeUsers(Long activityId) {
        activityRepository.findById(activityId)
                .orElseThrow(() -> new ActivityNotFoundException(activityId));
        return activityRepository.getUserWithOutcomeAnswers(activityId);
    }

}

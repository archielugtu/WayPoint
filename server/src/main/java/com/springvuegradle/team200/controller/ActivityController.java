package com.springvuegradle.team200.controller;

import com.springvuegradle.team200.dto.request.*;
import com.springvuegradle.team200.dto.response.*;
import com.springvuegradle.team200.model.*;
import com.springvuegradle.team200.repository.ActivityRepository;
import com.springvuegradle.team200.repository.HashtagRepository;
import com.springvuegradle.team200.service.*;
import com.springvuegradle.team200.validator.ValidationErrorBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.util.Base64Utils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.*;

@RestController
public class ActivityController {

    @Autowired
    HashtagRepository hashTagRepository;
    @Autowired
    UserActivityService userActivityService;
    @Autowired
    ActivityRepository activityRepository;
    @Autowired
    ActivityRolesService activityRolesService;
    @Autowired
    ActivityHistoryService activityHistoryService;
    @Autowired
    OutcomeService outcomeService;
    @Autowired
    ActivityImageService activityImageService;


    /**
     * Handles GET /profiles/{userId}/emails, retrieves a user's emails from the database
     *
     * @param userId User's ID
     * @return Activity types associated with the user
     */
    @GetMapping("/profiles/{userId}/activity-types")
    public ResponseEntity<List<String>> getUserActivities(@PathVariable("userId") Long userId) {
        List<String> activityTypes = userActivityService.readActivityTypes(userId);
        return ResponseEntity.ok(activityTypes);
    }

    /**
     * Handles GET /activities/types, retrieves list of all activity types a user can have
     *
     * @return List of all activity types
     */
    @GetMapping("/activities/types")
    @ResponseBody
    public ResponseEntity<List<String>> getActivityTypes() {
        List<String> activityTypes = userActivityService.readAll();
        return ResponseEntity.ok(activityTypes);
    }

    @PreAuthorize("#userId == principal.getId() OR principal.getIsAdmin()")
    @DeleteMapping("/profiles/{userId}/activities/{activityId}")
    public ResponseEntity<Void> deleteUserActivity(@PathVariable("userId") Long userId,
                                                   @PathVariable("activityId") Long activityId) {
        userActivityService.delete(userId, activityId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PreAuthorize("#userId == principal.getId() OR principal.getIsAdmin()")
    @PostMapping("/profiles/{userId}/activities")
    public ResponseEntity<?> createUserActivity(@RequestBody @Valid ActivityRequest activityRequest,
                                                Errors errors,
                                                @PathVariable("userId") Long userId) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(ValidationErrorBuilder.fromBindingErrors(errors));
        }

        Long activityId = userActivityService
                .create(userId, activityRequest)
                .getId();
        activityHistoryService.create(userId, activityId, Action.CREATED);
        return ResponseEntity.status(HttpStatus.CREATED).body(activityId);
    }

    @PreAuthorize("@activityRolesService.hasOrganiserOrGreaterRole(authentication, #activityId) OR principal.getIsAdmin()")
    @PutMapping("/profiles/{userId}/activities/{activityId}")
    public ResponseEntity<?> editUserActivity(@Valid @RequestBody ActivityRequest activityRequest,
                                              Errors errors,
                                              Authentication authentication,
                                              @PathVariable("userId") Long userId,
                                              @PathVariable("activityId") Long activityId) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(ValidationErrorBuilder.fromBindingErrors(errors));
        }

        activityId = userActivityService
                .edit(activityId, userId, activityRequest)
                .getId();

        activityHistoryService.create(userId, activityId, Action.UPDATED);

        return ResponseEntity.status(HttpStatus.OK).body(activityId);
    }

    /**
     * Handles PUT request to /profiles/{userId}/activity-types, updates a user's activity types
     *
     * @param activityTypeRequest Request containing specified activities
     * @param userId              Id of the user
     * @param errors              Errors found in the request
     * @return status code 200 if ok, 400 if not valid, 404 if user not found, 500 otherwise
     */
    @PreAuthorize("#userId == principal.getId() OR principal.getIsAdmin()")
    @PutMapping("/profiles/{userId}/activity-types")
    public ResponseEntity<?> editUserActivityTypes(@RequestBody @Valid ActivityTypeRequest activityTypeRequest,
                                                   Errors errors,
                                                   @PathVariable("userId") Long userId) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(ValidationErrorBuilder.fromBindingErrors(errors));
        }

        userActivityService.editUserActivityTypes(activityTypeRequest, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/profiles/{userId}/activities")
    public ResponseEntity<List<ActivityResponse>> getAllActivitiesByUser(@PathVariable("userId") Long userId) {
        List<ActivityResponse> activityResponses = userActivityService.readByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(activityResponses);
    }

    @GetMapping("/profiles/{userId}/activities/created")
    public ResponseEntity<List<ActivityResponse>> getActivitiesCreatedByUser(@PathVariable("userId") Long userId) {
        List<ActivityResponse> activityResponses = userActivityService.readCreatedActivitiesByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(activityResponses);
    }

    @PreAuthorize("@activityRolesService.hasCorrectRolePerVisibility(authentication, #activityId) OR principal.getIsAdmin()")
    @GetMapping("/activities/{activityId}")
    public ResponseEntity<ActivityResponse> getActivitiesByActivityId(@PathVariable("activityId") Long activityId,
                                                                      Authentication authentication) {
        Long callerId = ((User) authentication.getPrincipal()).getId();
        Activity activity = userActivityService.readByActivityId(activityId);
        int activityRole = activityRepository.findActivityRole(callerId, activityId);
        return ResponseEntity.status(HttpStatus.OK).body(new ActivityResponse(activity, activityRole));
    }

    /**
     * Retrieves a user's role in an activity
     *
     * @param activityId Unique ID of the activity
     * @param userId     Unique ID of the user
     * @return User's permission in an activity
     */
    @GetMapping("/activities/{activityId}/role/{userId}")
    public ResponseEntity<ActivityRoleResponse> getActivityRoleByActivityId(@PathVariable("activityId") Long activityId,
                                                                            @PathVariable("userId") Long userId) {
        ActivityRole activityRole = userActivityService.getUserRole(userId, activityId);
        return ResponseEntity.status(HttpStatus.OK).body(new ActivityRoleResponse(activityRole));
    }

    /**
     * Retrieves API caller's role in an activity
     *
     * @param activityId Unique ID of the activity
     * @return Caller's permission in an activity
     */
    @GetMapping("/activities/{activityId}/role")
    public ResponseEntity<ActivityRoleResponse> getActivityRoleByActivityId(@PathVariable("activityId") Long activityId,
                                                                            Authentication authentication) {
        Long userId = ((User) authentication.getPrincipal()).getId();
        ActivityRole activityRole = userActivityService.getUserRole(userId, activityId);
        return ResponseEntity.status(HttpStatus.OK).body(new ActivityRoleResponse(activityRole));
    }

    /**
     * Retrieves the number of people (participants, organisers, followers) involved with an activity
     *
     * @param activityId Unique ID of the activity
     * @return Number of organisers, organisers and followers of an activity
     */
    @GetMapping("/activities/{activityId}/headcount")
    public ResponseEntity<ActivityUsersCountResponse> getActivityUsersCount(@PathVariable("activityId") Long activityId) {
        var response = userActivityService.countUsersByActivityId(activityId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Retrieves the users of people (participants, organisers, followers) involved in an activity
     *
     * @param activityId Unique ID of the activity
     * @return Number of organisers, organisers and followers of an activity
     */
    @GetMapping("/activities/{activityId}/users")
    public ResponseEntity<Page<UserRoleDTO>> fetchUsersRelatedToActivity(@PathVariable("activityId") Long activityId,
                                                                         @RequestParam(defaultValue = "0") int page,
                                                                         @RequestParam(defaultValue = "10") int size) {
        var response = userActivityService.readAllUsersRelatedToActivity(activityId, page, size);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/activities/hashtags")
    public ResponseEntity<List<String>> getActivityHashtags() {
        List<String> hashtags = hashTagRepository.getHashtagsOrderedByActivities();
        return ResponseEntity.status(HttpStatus.OK).body(hashtags);
    }

    /**
     * Handles a GET request to /activities, which returns a list of activities meeting the search criteria
     *
     * @param searchActivityRequest Request containing specified criteria
     * @param errors                Errors found in the request
     * @return status code 200 if ok, 500 otherwise
     */
    @GetMapping("/activities")
    @ResponseBody
    public ResponseEntity<?> getActivities(@Valid SearchActivityRequest searchActivityRequest,
                                           Errors errors,
                                           Authentication authentication) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(ValidationErrorBuilder.fromBindingErrors(errors));
        }  // Restrict to 8 users per page of results
        Long callerId = ((User) authentication.getPrincipal()).getId();

        Page<SearchActivityResponse> resultPage = userActivityService.readByQuery(searchActivityRequest, callerId);
        List<SearchActivityResponse> dateList = new ArrayList<>(resultPage.getContent());
        dateList.sort(Comparator.comparing(SearchActivityResponse::getCreationDate));
        Collections.reverse(dateList);

        SearchActivityPagedResponse searchActivityPagedResponse = new SearchActivityPagedResponse(
                dateList, resultPage.getTotalPages()
        );
        return ResponseEntity.status(HttpStatus.OK).body(searchActivityPagedResponse);
    }

    @GetMapping("/activities/{activityId}/spec")
    @ResponseBody
    public ResponseEntity<List<ActivityResultSpecResponse>> getActivitiesSpec(@PathVariable("activityId") Long activityId) {
        List<ActivityResultSpecResponse> activityResultSpecResponses = userActivityService.readActivitySpec(activityId);
        return ResponseEntity.status(HttpStatus.OK).body(activityResultSpecResponses);
    }

    /**
     * Handles GET user activity response, retrieves the user's activity following status
     *
     * @return User's activity following status response
     */
    @GetMapping("/profiles/{userId}/subscriptions/activities/{activityId}")
    @ResponseBody
    public ResponseEntity<ActivityFollowingStatusResponse> getUserActivityFollowingStatus(@PathVariable("userId") Long userId,
                                                                                          @PathVariable("activityId") Long activityId) {
        ActivityFollowingStatusResponse statusResponse = userActivityService.retrieveUserActivityFollowingStatus(userId, activityId);
        return ResponseEntity.status(HttpStatus.OK).body(statusResponse);
    }

    /**
     * Handles POST edit role request, modifies the user's role for the given activity
     *
     * @param userId                  The ID of the user to change the role of
     * @param activityId              The ID of the activity which the user wants to change their role for
     * @param editActivityRoleRequest An EditActivityRoleRequest object containing the user's new role info
     * @param errors                  Errors found in the request
     * @return status code 200 if ok,
     * 400 if not valid,
     * 403 if changing creator's role,
     * 404 if user not found,
     * 500 otherwise
     */
    @PreAuthorize("@activityRolesService.hasValidRole(authentication, #activityId, #editActivityRoleRequest) OR principal.getIsAdmin()")
    @PostMapping("/profiles/{userId}/subscriptions/activities/{activityId}")
    public ResponseEntity<?> editRoleRequest(@RequestBody @Valid EditActivityRoleRequest editActivityRoleRequest,
                                             Errors errors,
                                             @PathVariable("userId") Long userId,
                                             @PathVariable("activityId") Long activityId) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(ValidationErrorBuilder.fromBindingErrors(errors));
        }

        ActivityRole targetRole = editActivityRoleRequest.getRole();

        if (targetRole == ActivityRole.CREATOR) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        userActivityService.editUserRole(userId, activityId, editActivityRoleRequest);

        switch (targetRole) {
            case FOLLOWER:
                activityHistoryService.create(userId, activityId, Action.FOLLOWED);
                break;
            case PARTICIPANT:
                activityHistoryService.create(userId, activityId, Action.PARTICIPATED);
                break;
            case ORGANISER:
                activityHistoryService.create(userId, activityId, Action.UPDATED);
                break;
            default:
                break;
        }
        return ResponseEntity.ok().build();
    }

    /**
     * Handles POST edit role request, modifies the participator's role for the given activity
     * that the activity creator has specified
     *
     * @param creatorId               The ID of the activity creator
     * @param activityId              The ID of the activity which the user wants to change their role for
     * @param userId                  The ID of the user to change the role of by the activity creator
     * @param editActivityRoleRequest An EditActivityRoleRequest object containing the user's new role info
     * @param errors                  Errors found in the request
     * @return status code 200 if ok,
     *                     400 if not valid,
     *                     403 if changing creator's role,
     *                     404 if user not found,
     *                     500 otherwise
     */
    @PreAuthorize("#creatorId == principal.getId() OR principal.getIsAdmin()")
    @PostMapping("/profiles/{creatorId}/subscriptions/activities/{activityId}/{userId}")
    public ResponseEntity<?> creatorEditUserRoleRequest(@RequestBody @Valid EditActivityRoleRequest editActivityRoleRequest,
                                                        Errors errors,
                                                        @PathVariable("creatorId") Long creatorId,
                                                        @PathVariable("userId") Long userId,
                                                        @PathVariable("activityId") Long activityId) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(ValidationErrorBuilder.fromBindingErrors(errors));
        }

        ActivityRole targetRole = editActivityRoleRequest.getRole();

        if (targetRole == ActivityRole.CREATOR) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Changes the role of the activity PARTICIPATOR and not the activity creator
        userActivityService.editUserRole(userId, activityId, editActivityRoleRequest);

        switch (targetRole) {
            case FOLLOWER:
                activityHistoryService.create(creatorId, activityId, Action.FOLLOWED);
                break;
            case PARTICIPANT:
                activityHistoryService.create(creatorId, activityId, Action.PARTICIPATED);
                break;
            default:
                break;
        }
        return ResponseEntity.ok().build();
    }

    /**
     * Handles DELETE role request, deletes the user's role for the given activity
     *
     * @param userId     The ID of the user to delete the role of
     * @param activityId The ID of the activity which the user wants to delete their role for
     * @return status code 200 if ok, 400 if not valid, 404 if user not found, 500 otherwise
     */
    @PreAuthorize("#userId == principal.getId() OR principal.getIsAdmin()")
    @DeleteMapping("/profiles/{userId}/subscriptions/activities/{activityId}")
    public ResponseEntity<Void> deleteRole(@PathVariable("userId") Long userId,
                                           @PathVariable("activityId") Long activityId) {
        activityHistoryService.createIfRequired(userId, activityId, Action.UNFOLLOWED);
        activityHistoryService.createIfRequired(userId, activityId, Action.NOTPARTICIPATE);
        userActivityService.deleteUserRole(userId, activityId);
        return ResponseEntity.ok().build();
    }

    /**
     * Handles GET activity history response
     * <p>
     * It retrieves activities updates with associated timestamps when:
     * - the activity followed by the user is UPDATED,
     * - the user started/stopped FOLLOWING an activity
     *
     * @param userId Unique ID of user we want to retrieve history of
     * @return status code 200 if ok
     * 404 if user or activity not found
     * 500 otherwise
     */
    @GetMapping("/profiles/{userId}/subscriptions/activities/history")
    @ResponseBody
    public ResponseEntity<ActivityHistoryResponse> getActivityHistory(@PathVariable("userId") Long userId,
                                                                      @RequestParam(defaultValue = "0") int page,
                                                                      @RequestParam(defaultValue = "10") int size) {
        Page<SingleHistoryResponse> activityHistories = activityHistoryService.readByUserId(userId, page, size);
        return ResponseEntity.status(HttpStatus.OK).body(ActivityHistoryResponse.of(activityHistories));
    }

    @PreAuthorize("#userId == principal.getId() OR principal.getIsAdmin()")
    @GetMapping("/profiles/{userId}/activities/all")
    @ResponseBody
    public ResponseEntity<List<ActivityResponse>> getAllUserRelatedActivities(@PathVariable("userId") Long userId) {
        List<ActivityResponse> userRelatedActivities = userActivityService.readByUserId(userId);

        return ResponseEntity.status(HttpStatus.OK).body(userRelatedActivities);
    }

    /**
     * Retrieves list of participants in an activity
     *
     * @param activityId Activity's unique ID we are interested in
     * @return status code 200 if ok
     *                     404 if activity not found
     *                     500 otherwise
     */
    @GetMapping("/activities/{activityId}/participants")
    @ResponseBody
    public ResponseEntity<ActivityParticipantsResponse> getActivityParticipants(@PathVariable("activityId") Long activityId,
                                                                                @RequestParam(defaultValue = "0") int page,
                                                                                @RequestParam(defaultValue = "10") int size) {
        Page<CompactUserResponse> users = userActivityService.readByParticipants(size, activityId, page);
        ActivityParticipantsResponse activityParticipantsResponse = new ActivityParticipantsResponse();
        activityParticipantsResponse.setTotalPages(users.getTotalPages());
        activityParticipantsResponse.setTotalUsers(users.getNumberOfElements());
        activityParticipantsResponse.setParticipants(users.getContent());
        return ResponseEntity.status(HttpStatus.OK).body(activityParticipantsResponse);
    }

    /**
     * Retrieves list of followers in an activity
     *
     * @param activityId Activity's unique ID we are interested in
     * @return status code 200 if ok
     *                     404 if activity not found
     *                     500 otherwise
     */
    @GetMapping("/activities/{activityId}/followers")
    @ResponseBody
    public ResponseEntity<ActivityFollowersResponse> getActivityFollowers(@PathVariable("activityId") Long activityId,
                                                                          @RequestParam(defaultValue = "0") int page,
                                                                          @RequestParam(defaultValue = "10") int size) {
        Page<CompactUserResponse> users = userActivityService.readByFollowers(size, activityId, page);
        ActivityFollowersResponse activityUsersPagedResponse = new ActivityFollowersResponse();
        activityUsersPagedResponse.setTotalPages(users.getTotalPages());
        activityUsersPagedResponse.setTotalUsers(users.getNumberOfElements());
        activityUsersPagedResponse.setFollowers(users.getContent());

        return ResponseEntity.status(HttpStatus.OK).body(activityUsersPagedResponse);
    }

    /**
     * Retrieves list of organisers in an activity
     *
     * @param activityId Activity's unique ID we are interested in
     * @return status code 200 if ok
     *                     404 if activity not found
     *                     500 otherwise
     */
    @GetMapping("/activities/{activityId}/organisers")
    @ResponseBody
    public ResponseEntity<ActivityOrganisersResponse> getActivityOrganisers(@PathVariable("activityId") Long activityId,
                                                                            @RequestParam(defaultValue = "0") int page,
                                                                            @RequestParam(defaultValue = "10") int size) {
        Page<CompactUserResponse> users = userActivityService.readByOrganisers(size, activityId, page);
        ActivityOrganisersResponse activityOrganisersResponse = new ActivityOrganisersResponse();
        activityOrganisersResponse.setTotalPages(users.getTotalPages());
        activityOrganisersResponse.setTotalUsers(users.getNumberOfElements());
        activityOrganisersResponse.setOrganisers(users.getContent());

        return ResponseEntity.status(HttpStatus.OK).body(activityOrganisersResponse);
    }

    /**
     * Edits the activity sharing visibility of an activity
     *
     * @param activityVisibilityRequest The request containing specified visibility type, and list of accessors
     * @param errors                    Errors found in the request
     * @param userId                    User's unique ID
     * @param activityId                Activity's unique ID
     * @return status code 200, if activity was updated successfully
     */
    @PreAuthorize("@activityRolesService.hasOrganiserOrGreaterRole(authentication, #activityId) OR principal.getIsAdmin()")
    @PutMapping("/profiles/{userId}/activities/{activityId}/visibility")
    public ResponseEntity<?> editActivityVisibility(@Valid @RequestBody ActivityVisibilityRequest activityVisibilityRequest,
                                                    Errors errors,
                                                    @PathVariable("userId") Long userId,
                                                    @PathVariable("activityId") Long activityId) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(ValidationErrorBuilder.fromBindingErrors(errors));
        }
        userActivityService.editActivityVisibility(activityId, activityVisibilityRequest);
        activityHistoryService.create(userId, activityId, Action.UPDATED);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PreAuthorize("#userId == principal.getId() OR principal.getIsAdmin()")
    @PutMapping("/activity/{activityId}/outcomes/{userId}/answers")
    @ResponseBody
    public ResponseEntity<Void> putOutcomeAnswers(@RequestBody UserOutcomeRequest userOutcomeRequest,
                                                  @PathVariable("activityId") Long activityId,
                                                  @PathVariable("userId") Long userId) {
        outcomeService.editOutcomeUserResponse(userOutcomeRequest, activityId, userId);
        activityHistoryService.create(userId, activityId, Action.ADDEDRESULTS);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/activity/{activityId}/outcomes/{userId}/answers")
    @ResponseBody
    public ResponseEntity<UserOutcomeRequest> getOutcomeAnswers(@PathVariable("activityId") Long activityId,
                                                                @PathVariable("userId") Long userId) {
        UserOutcomeRequest answers = outcomeService.getOutcomeUserResponses(activityId, userId);
        return ResponseEntity.ok().body(answers);
    }

    /**
     * Handles a GET request which returns a list of users that have answered the results to a specific activity
     * * @return status code 200 if ok, 500 otherwise
     */
    @GetMapping("/activities/{activityId}/outcomes/users")
    @ResponseBody
    public ResponseEntity<Set<UserResponse>> getOutcomeUsers(@PathVariable("activityId") Long activityId) {


        Set<UserResponse> userResponses = userActivityService.getActivityOutcomeUsers(activityId);

        return ResponseEntity.status(HttpStatus.OK).body(userResponses);
    }

    /**
     * Handles a GET request which returns a list of photos for a specific activity
     *
     * @param activityId The ID of the activity which the user wants to get the photos from
     * @return status code 200 if ok, 500 otherwise
     */
    @GetMapping("/activities/{activityId}/photos")
    @ResponseBody
    public ResponseEntity<PhotoResponse> getActivityPhotos(@PathVariable("activityId") Long activityId) {
        PhotoResponse photoResponse = activityImageService.readToDTO(activityId);
        return ResponseEntity.status(HttpStatus.OK).body(photoResponse);
    }

    /**
     * Handles a GET request which returns a single photo for a specific activity
     *
     * @param activityId The ID of the activity which the user wants to get the photo from
     * @param photoId    The ID of the photo which the user wants to get the photos from
     * @return status code 200 if ok, 500 otherwise
     */
    @GetMapping("/activities/{activityId}/photos/{photoId}")
    @ResponseBody
    public ResponseEntity<String> getActivitySinglePhoto(@PathVariable("activityId") Long activityId,
                                                         @PathVariable("photoId") Long photoId) {
        PhotoDTO dto = activityImageService.readBinary(activityId, photoId);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", dto.getMediaType().toString());

        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(responseHeaders)
                .body(Base64Utils.encodeToString(dto.getData()));
    }

    /**
     * Handles a POST request which uploads a single photo for a specific activity
     *
     * @param activityId  The ID of the activity which the user wants to get the photo from
     * @param contentType The image type of the activity that will be uploaded
     * @param image       The image of the activity that will be uploaded
     * @return status code 200 if ok, 500 otherwise
     */
    @PreAuthorize("@activityRolesService.hasOrganiserOrGreaterRole(authentication, #activityId) OR principal.getIsAdmin()")
    @PostMapping("/activities/{activityId}/photos")
    @ResponseBody
    public ResponseEntity<Long> uploadActivityPhoto(@PathVariable("activityId") Long activityId,
                                                    @RequestHeader("Content-Type") String contentType,
                                                    @RequestBody byte[] image) {
        PhotoRequest photoRequest = new PhotoRequest();
        photoRequest.setActivityId(activityId);
        photoRequest.setImage(image);
        photoRequest.setMediaType(MediaType.parseMediaType(contentType));
        photoRequest.setPrimary(false); // set to False by default

        Photo photo = activityImageService.create(photoRequest);
        return ResponseEntity.status(HttpStatus.OK).body(photo.getId());
    }

    /**
     * Handles a PUT request which sets a primary photo for a specific activity
     *
     * @param activityId The ID of the activity which the user wants to get the set primary photo
     * @return status code 200 if ok, 500 otherwise
     */
    @PreAuthorize("@activityRolesService.hasOrganiserOrGreaterRole(authentication, #activityId) OR principal.getIsAdmin()")
    @PutMapping("/activities/{activityId}/photos/{photoId}/primary")
    @ResponseBody
    public ResponseEntity<Long> uploadActivityPhoto(@PathVariable("photoId") Long photoId,
                                                    @PathVariable("activityId") Long activityId) {
        activityImageService.update(activityId, photoId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * Handles a GET request to retrieve an activity's hero (primary) image
     *
     * @param activityId ID of the activity
     * @return status code 200 if ok, 404 if image does not exist, 500 otherwise
     */
    @GetMapping("/activities/{activityId}/photos/primary")
    @ResponseBody
    public ResponseEntity<byte[]> retrieveActivityHeroImg(@PathVariable("activityId") Long activityId) throws IOException {
        byte[] heroImage = activityImageService.readHeroImage(activityId);
        return ResponseEntity.status(HttpStatus.OK).body(heroImage);
    }

    /**
     * Handles DELETE photo request, deletes a specific activity's photo
     *
     * @param activityId The ID of the activity which the user wants to delete the photo for
     * @param photoId    The ID of the photo that will be deleted
     * @return status code 200 if ok, 400 if not valid, 404 if user not found, 500 otherwise
     */
    @PreAuthorize("@activityRolesService.hasOrganiserOrGreaterRole(authentication, #activityId) OR principal.getIsAdmin()")
    @DeleteMapping("/activities/{activityId}/photos/{photoId}")
    public ResponseEntity<Void> deleteActivityPhoto(@PathVariable("activityId") Long activityId,
                                                    @PathVariable("photoId") Long photoId) {
        activityImageService.delete(activityId, photoId);
        return ResponseEntity.ok().build();
    }
}

package com.springvuegradle.team200.service;

import com.springvuegradle.team200.dto.request.ActivityRequest;
import com.springvuegradle.team200.dto.request.EditActivityRoleRequest;
import com.springvuegradle.team200.exception.ActivityNotFoundException;
import com.springvuegradle.team200.model.Activity;
import com.springvuegradle.team200.model.ActivityRole;
import com.springvuegradle.team200.model.ActivityVisibility;
import com.springvuegradle.team200.model.User;
import com.springvuegradle.team200.repository.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * Handles preauth checks
 */
@Service
public class ActivityRolesService {

    @Autowired
    ActivityRepository dao;

    /**
     * Checks whether the role given is creator or organiser
     *
     * @param activityRole integer representation of the role
     * @return True if creator or organiser of activity, false otherwise
     */
    private boolean isCreatorOrOrganiser(int activityRole) {
        return ActivityRole.CREATOR.ordinal() == activityRole || ActivityRole.ORGANISER.ordinal() == activityRole;
    }

    /**
     * Checks whether the role given is creator, organiser, participant or follower
     *
     * @param activityRole integer representation of the role
     * @return True if creator or organiser of activity, false otherwise
     */
    private boolean hasAnyRoleInActivity(int activityRole) {
        return ActivityRole.NONE.ordinal() != activityRole;
    }

    /**
     * Checks whether the role given is creator
     *
     * @param activityRole integer representation of the role
     * @return True if creator of activity, false otherwise
     */
    private boolean isCreator(int activityRole) {
        return ActivityRole.CREATOR.ordinal() == activityRole;
    }

    /**
     * Checks whether the role given is follower or participant
     *
     * @param activityRole integer representation of the role
     * @return True if follower or participant of activity, false otherwise
     */
    private boolean hasNoEscalatedPrivileges(int activityRole) {
        return ActivityRole.NONE.ordinal() == activityRole || ActivityRole.FOLLOWER.ordinal() == activityRole
                || ActivityRole.PARTICIPANT.ordinal() == activityRole;
    }

    /**
     * Checks whether a user has valid role to change their role in a certain activity
     *
     * @param authentication Authentication object that is set automatically by
     *                       Spring this refers to the user calling this method
     * @param activityId     Unique ID of activity we are checking the role of
     * @param request        Request obj containing new role
     * @return True if user has valid role to execute changing role, False otherwise
     **/
    @Transactional
    public boolean hasValidRole(Authentication authentication, Long activityId, EditActivityRoleRequest request) {
        if (authentication == null || activityId == null) {
            return false;
        }

        Long callerId = ((User) authentication.getPrincipal()).getId();
        int currentRole = dao.findActivityRole(callerId, activityId);

        ActivityRole targetRole = request.getRole();

        if (isCreatorOrOrganiser(targetRole.ordinal())) {
            return isCreatorOrOrganiser(currentRole);
        } else if (hasNoEscalatedPrivileges(targetRole.ordinal())) {
            return hasNoEscalatedPrivileges(currentRole) || ActivityRole.ORGANISER.ordinal() == currentRole;
        }

        return false;
    }

    /**
     * Checks whether a user has valid role to edit an activity
     *
     * @param authentication Authentication object that is set automatically by
     *                       Spring this refers to the user calling this method
     * @param activityId     Unique ID of activity we are checking the role of
     * @return True if user has valid role to execute edting an activity, False otherwise
     **/
    @Transactional
    public boolean hasOrganiserOrGreaterRole(Authentication authentication, Long activityId) {

        if (authentication == null || activityId == null) {
            return false;
        }

        Long callerId = ((User) authentication.getPrincipal()).getId();
        int currentRole = dao.findActivityRole(callerId, activityId);

        return isCreatorOrOrganiser(currentRole);
    }

    /**
     * Checks whether a user has valid role to get an activity
     *
     * @param authentication Authentication object that is set automatically by
     *                       Spring this refers to the user calling this method
     * @param activityId     Unique ID of activity we are checking the role of
     * @return True if user has valid role to execute get an activity, False otherwise
     **/
    @Transactional
    public boolean hasCorrectRolePerVisibility(Authentication authentication, Long activityId) {
        if (authentication == null || activityId == null) {
            return false;
        }
        Activity activity = dao
                .findById(activityId)
                .orElseThrow(() -> new ActivityNotFoundException(activityId));
        Long callerId = ((User) authentication.getPrincipal()).getId();
        int currentRole = dao.findActivityRole(callerId, activityId);
        ActivityVisibility visibility = activity.getVisibility();
        if (visibility == ActivityVisibility.PRIVATE) {
            return isCreator(currentRole);
        } else if (visibility == ActivityVisibility.RESTRICTED) {
            return hasAnyRoleInActivity(currentRole);
        }

        return true;
    }
}

package com.springvuegradle.team200.service;

import com.springvuegradle.team200.dto.response.SingleHistoryResponse;
import com.springvuegradle.team200.exception.ActivityNotFoundException;
import com.springvuegradle.team200.exception.UserNotFoundException;
import com.springvuegradle.team200.model.*;
import com.springvuegradle.team200.repository.ActivityRepository;
import com.springvuegradle.team200.repository.UserActivityHistoryRepository;
import com.springvuegradle.team200.repository.UserRepository;
import com.springvuegradle.team200.repository.specifications.ActivityHistorySpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.List;

@Service
public class ActivityHistoryService {

    @Autowired
    UserActivityHistoryRepository dao;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ActivityRepository activityRepository;

    /**
     * Creates a new history entry in the database
     * <p>
     * The entry contains user's action with an activity at current time
     *
     * @param userId     Unique ID of user that did the action with the activity
     * @param activityId Unique ID of activity in question
     * @param action     Action done
     */
    @Transactional
    public void create(Long userId, Long activityId, Action action) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new ActivityNotFoundException(activityId));

        UserActivityHistory history = new UserActivityHistory();
        history.setUser(user);
        history.setActivity(activity);
        history.setAction(action);
        history.setTimestamp(Instant.now());

        dao.save(history);
    }

    /**
     * Creates a new history entry in the database if the user was a follower before and
     * un-follows the activity
     *
     * @param userId     Unique ID of user that did the action with the activity
     * @param activityId Unique ID of activity in question
     * @param action     Action done
     */
    @Transactional
    public void createIfRequired(Long userId, Long activityId, Action action) {
        int currentRole = activityRepository.findActivityRole(userId, activityId);

        if (currentRole == ActivityRole.FOLLOWER.ordinal() && action == Action.UNFOLLOWED) {
            create(userId, activityId, action);
        }

        if (currentRole == ActivityRole.PARTICIPANT.ordinal() && action == Action.NOTPARTICIPATE) {
            create(userId, activityId, action);
        }
    }

    /**
     * Retrieves the list of changes done for a particular activity
     * This is used to populate user's home feed
     *
     * @param userId User's unique ID
     * @param page   Current page - starts from 0
     * @param size   Number of items per page
     * @return The list of history of an activity
     */
    @Transactional
    public Page<SingleHistoryResponse> readByUserId(Long userId, int page, int size) {

        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        List<Long> followedActivityIds = userRepository.findFollowingActivityIds(userId);
        List<Long> participatedActivityIds = userRepository.findParticipatingActivityIds(userId);

        Specification<UserActivityHistory> spec = Specification.where(
                // Retrieves rows with activities that user is participating
                ActivityHistorySpec.hasActivityIdsIn(participatedActivityIds)
        ).or(
                // Retrieve rows with activities followed by user
                ActivityHistorySpec.hasActivityIdsIn(followedActivityIds)
        ).or(
                // Retrieve rows containing follow and unfollow
                ActivityHistorySpec.isUpdatingFollowStatusByUserId(userId)
        ).or(
                // Retrieves rows containing participating and not participating
                ActivityHistorySpec.isUpdatingParticipatingStatusByUserId(userId)
        ).and(
                // Avoid seeing updates from ourselves
                ActivityHistorySpec.isNotUpdateFromUserId(userId)
        );

        return dao.findAll(
                spec,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"))
        ).map(SingleHistoryResponse::of);
    }


}

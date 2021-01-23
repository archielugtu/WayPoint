package com.springvuegradle.team200.repository.specifications;

import com.springvuegradle.team200.model.Action;
import com.springvuegradle.team200.model.UserActivityHistory;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;


public class ActivityHistorySpec {

    private ActivityHistorySpec() {
        //left empty intentionally
    }

    private static final String ACTION_COLUMN = "action";

    /**
     * Returns TRUE if an activity history entry is one of the activity IDs specified
     *
     * @param activityIds List of activity Ids to search for
     */
    public static Specification<UserActivityHistory> hasActivityIdsIn(List<Long> activityIds) {
        return (root, query, criteriaBuilder) -> {
            List<Action> events = List.of(Action.UPDATED, Action.CREATED, Action.ADDEDRESULTS);
            query.distinct(true);
            return criteriaBuilder.and(
                    root.get("activity").get("id").in(activityIds),
                    root.get(ACTION_COLUMN).in(events)
            );
        };
    }

    /**
     * Returns TRUE if the history contains FOLLOWED or UNFOLLOWED action by user ID specified
     *
     * @param userId User's ID to search for
     */
    public static Specification<UserActivityHistory> isUpdatingFollowStatusByUserId(Long userId) {
        return (root, query, criteriaBuilder) -> {
            query.distinct(true);
            return criteriaBuilder.and(
                    criteriaBuilder.equal(root.get("user").get("id"), userId),
                    criteriaBuilder.or(
                            criteriaBuilder.equal(root.get(ACTION_COLUMN), Action.FOLLOWED),
                            criteriaBuilder.equal(root.get(ACTION_COLUMN), Action.UNFOLLOWED)
                    )
            );
        };
    }

    /**
     * Returns TRUE if the history contains PARTICIPATED or NOTPARTICIPATE action by user ID specified
     *
     * @param userId User's ID to search for
     */
    public static Specification<UserActivityHistory> isUpdatingParticipatingStatusByUserId(Long userId) {
        return (root, query, criteriaBuilder) -> {
            query.distinct(true);
            return criteriaBuilder.and(
                    criteriaBuilder.equal(root.get("user").get("id"), userId),
                    criteriaBuilder.or(
                            criteriaBuilder.equal(root.get(ACTION_COLUMN), Action.PARTICIPATED),
                            criteriaBuilder.equal(root.get(ACTION_COLUMN), Action.NOTPARTICIPATE)
                    )
            );
        };
    }

    /**
     * Returns TRUE if the history does not contain UPDATED or CREATED action from user ID specified
     *
     * @param userId User's ID to filter out
     */
    public static Specification<UserActivityHistory> isNotUpdateFromUserId(Long userId) {
        List<Action> blackList = List.of(Action.UPDATED, Action.CREATED);
        return (root, query, criteriaBuilder) -> {
            query.distinct(true);
            return criteriaBuilder.not(
                    criteriaBuilder.and(
                            criteriaBuilder.isNotNull(root.get("activity").get("creator")),
                            criteriaBuilder.and(
                                    criteriaBuilder.equal(
                                            root.get("user").get("id"),
                                            userId
                                    ),
                                    root.get(ACTION_COLUMN).in(blackList)
                            )
                    )
            );
        };
    }
}

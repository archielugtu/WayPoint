package com.springvuegradle.team200.repository.specifications;

import com.springvuegradle.team200.model.*;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.SetJoin;
import java.util.Date;
import java.util.List;

public class ActivitySpec {

    private ActivitySpec() {
        //left empty intentionally
    }

    /**
     * Whether the activity has the hashtag id specified
     *
     * @param hashTagId The id of the hashtag being identified
     */
    public static Specification<Activity> hasHashTagId(Long hashTagId) {
        return (root, query, criteriaBuilder) -> {
            query.distinct(true);
            SetJoin<Activity, Hashtag> join = root.joinSet("hashtags");
            return criteriaBuilder.equal(join.get("hashtagId"), hashTagId);
        };
    }

    /**
     * Whether the activity has the activityName specified
     *
     * @param activityName The name of the activity being identified
     */
    public static Specification<Activity> hasActivityName(String activityName) {
        return (root, query, criteriaBuilder) -> {
            query.distinct(true);

            if (isSurroundedByQuotes(activityName)) {
                return criteriaBuilder.equal(
                        root.get("activityName"),
                        activityName.substring(1, activityName.length() - 1)
                );
            }

            return criteriaBuilder.like(root.get("activityName"), "%"+activityName+"%");
        };
    }

    /**
     * Whether the activity has the activity type id specified
     *
     * @param activityTypeId The id of the activity type being identified
     */
    public static Specification<Activity> hasActivityTypeId(Long activityTypeId) {
        return (root, query, criteriaBuilder) -> {
            query.distinct(true);
            SetJoin<Activity, ActivityType> join = root.joinSet("activityTypes");
            return criteriaBuilder.equal(join.get("activityTypeId"), activityTypeId);
        };
    }

    /**
     * Whether the activity has any of the hashtag ids specified
     *
     * @param hashtagId The list of hashtag ids
     */
    public static Specification<Activity> hasAnyHashTagId(List<Long> hashtagId) {
        return (root, query, criteriaBuilder) -> {
            query.distinct(true);

            SetJoin<Activity, Hashtag> hashtagListJoin = root.joinSet("hashtags");
            return hashtagListJoin.get("hashtagId").in(hashtagId);
        };
    }

    /**
     * Whether the activity has any of the activity type ids specified
     *
     * @param activityTypeIds The list of activity type ids
     */
    public static Specification<Activity> hasAnyActivityTypeId(List<Long> activityTypeIds) {
        return (root, query, criteriaBuilder) -> {
            query.distinct(true);

            ListJoin<Activity, ActivityType> activityTypesListJoin = root.joinList("activityTypes");
            return activityTypesListJoin.get("activityTypeId").in(activityTypeIds);
        };
    }

    /**
     * Whether the activity is publicly visible
     */
    public static Specification<Activity> hasPublicVisibility() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("visibility"), ActivityVisibility.PUBLIC);
    }



    /**
     * Finds all activities within bounds given that the
     * this statement only returns values IF the bounds DO NOT cross the meridian
     * The meridian is the point between longitude -179 and longitude +179
     */
    public static Specification<Activity> isActivityWithinBounds(Float sw_lat, Float sw_long, Float ne_lat, Float ne_long) {
        return (root, query, criteriaBuilder) -> {
            if (sw_lat == null || sw_long == null || ne_lat == null || ne_long == null) {
                return criteriaBuilder.or();  // Defines false
            }

            if (ne_long > sw_long) { // location boundaries DOES NOT overlap the meridian
                return criteriaBuilder.and(
                        criteriaBuilder.isNotNull(root.get("location").get("latitude")),
                        criteriaBuilder.isNotNull(root.get("location").get("longitude")),
                        criteriaBuilder.greaterThanOrEqualTo(root.get("location").get("latitude"), sw_lat),
                        criteriaBuilder.greaterThanOrEqualTo(root.get("location").get("longitude"), sw_long),
                        criteriaBuilder.lessThanOrEqualTo(root.get("location").get("latitude"), ne_lat),
                        criteriaBuilder.lessThanOrEqualTo(root.get("location").get("longitude"), ne_long));
            } else { // location boundaries DOES overlap the meridian
                return criteriaBuilder.and(
                        criteriaBuilder.isNotNull(root.get("location").get("latitude")),
                        criteriaBuilder.isNotNull(root.get("location").get("longitude")),
                        criteriaBuilder.greaterThanOrEqualTo(root.get("location").get("latitude"), sw_lat),
                        criteriaBuilder.lessThanOrEqualTo(root.get("location").get("latitude"), ne_lat),
                        criteriaBuilder.or(
                                criteriaBuilder.greaterThanOrEqualTo(root.get("location").get("longitude"), sw_long),
                                criteriaBuilder.lessThanOrEqualTo(root.get("location").get("longitude"), ne_long)
                        ));
            }
        };
    }

    /**
     * Whether the activity starts and ends within the specified dates
     *
     * @param startDate The starting date of the activity
     * @param endDate The ending date of the activity
     */
    public static Specification<Activity> activityInDates(Date startDate, Date endDate) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.or(
                    criteriaBuilder.and(
                            criteriaBuilder.lessThanOrEqualTo(root.get("startDate"), endDate),
                            criteriaBuilder.greaterThanOrEqualTo(root.get("endDate"),startDate)
                    ), criteriaBuilder.isTrue(root.get("isContinuous"))
                        );
    }

    /**
     * Whether the user is the owner of the activity
     *
     * @param userId The ID of the user
     */
    public static Specification<Activity> userIsOwner(Long userId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("creator"), userId);
    }

    /**
     * Whether the activity is shared with the user (follower, participant, or organiser)
     *
     * @param user The User to check if the activity is shared with
     */
    public static Specification<Activity> userIsShared(User user) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isMember(user, root.get("sharedUsers"));
    }

    /**
     * Checks if the query has quotes surrounding it
     * @param query The search query
     * @return returns true if surrounded by quote
     */
    private static boolean isSurroundedByQuotes(String query) {
        return (((query.startsWith("'") && query.endsWith("'")) ||
                (query.startsWith("\"") && query.endsWith("\"")))
                && (query.length() > 1));
    }
}

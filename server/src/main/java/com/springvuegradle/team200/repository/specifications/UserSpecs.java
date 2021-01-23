package com.springvuegradle.team200.repository.specifications;

import com.springvuegradle.team200.model.ActivityType;
import com.springvuegradle.team200.model.User;
import com.springvuegradle.team200.model.UserEmail;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.ListJoin;
import java.util.List;

/**
 * Defines User's specifications, basically how can we filter a user
 * <p>
 * Supports combining via OR and AND operators
 */
public class UserSpecs {

    private UserSpecs() {}

    private static final String ADDRESS_COLUMN = "address";

    /**
     * Whether it's a normal user without global admin privilege
     */
    public static Specification<User> isNotGlobalAdmin() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isFalse(root.get("isGlobalAdmin"));
    }

    /**
     * Whether user has activity type id specified
     */
    public static Specification<User> hasActivityTypeOfId(Long activityTypeId) {
        return (root, query, criteriaBuilder) -> {
            query.distinct(true);
            ListJoin<User, ActivityType> join = root.joinList("activityTypes");
            return criteriaBuilder.equal(join.get("activityTypeId"), activityTypeId);
        };
    }

    /**
     * Whether user has any of activity type id specified
     */
    public static Specification<User> hasAnyActivityTypeOfIds(List<Long> activityTypeIds) {
        return (root, query, criteriaBuilder) -> {
            query.distinct(true);

            ListJoin<User, ActivityType> userJoinActivityType = root.joinList("activityTypes");
            return userJoinActivityType.get("activityTypeId").in(activityTypeIds);
        };
    }

    /**
     * Whether user has first name specified
     * <p>
     * firstName is case insensitive
     */
    public static Specification<User> byFirstName(String firstName) {
        return (root, query, criteriaBuilder) -> {
            if (firstName == null || firstName.isBlank()) {
                return criteriaBuilder.and(); // defines TRUE
            }
            if (isSurroundedByQuotes(firstName)) {
                return criteriaBuilder.equal(
                        root.get("firstName"),
                        firstName.substring(1, firstName.length() - 1)
                );
            }
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("firstName")),
                    "%" + firstName.toLowerCase() + "%"
            );
        };
    }

    /**
     * Whether user has last name specified
     * <p>
     * lastName is case insensitive
     */
    public static Specification<User> byLastName(String lastName) {
        return (root, query, criteriaBuilder) -> {
            if (lastName == null || lastName.isBlank()) {
                return criteriaBuilder.and(); // defines TRUE
            }
            if (isSurroundedByQuotes(lastName)) {
                return criteriaBuilder.equal(
                        root.get("lastName"),
                        lastName.substring(1, lastName.length() - 1)
                );
            }
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("lastName")),
                    "%" + lastName.toLowerCase() + "%"
            );
        };
    }

    /**
     * Whether user with email has EXACT match with email parameter specified
     * <p>
     * email is case insensitive
     */
    public static Specification<User> byEmail(String email) {
        return (root, query, criteriaBuilder) -> {
            query.distinct(true);

            ListJoin<User, UserEmail> userJoinEmail = root.joinList("userEmails");
            if (email == null || email.isBlank()) {
                return criteriaBuilder.and(); // defines TRUE
            }
            if (isSurroundedByQuotes(email)) {
                return criteriaBuilder.equal(
                        userJoinEmail.get(ADDRESS_COLUMN),
                        email.substring(1, email.length() - 1)
                );
            }
            return criteriaBuilder.like(
                    criteriaBuilder.lower(userJoinEmail.get(ADDRESS_COLUMN)),
                    "%" + email.toLowerCase() + "%"
            );

        };
    }

    /**
     * Whether user does not have the user ID specified
     */
    public static Specification<User> userIdIsNot(Long userId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.notEqual(root.get("id"), userId);
    }

    /**
     * Whether user has any of the emails address specified
     */
    public static Specification<User> emailAddressIn(List<String> emails) {
        return (root, query, criteriaBuilder) -> {
            query.distinct(true);
            ListJoin<User, UserEmail> userJoinEmail = root.joinList("userEmails");
            return userJoinEmail.get(ADDRESS_COLUMN).in(emails);
        };
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

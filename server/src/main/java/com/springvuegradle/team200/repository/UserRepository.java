package com.springvuegradle.team200.repository;

import com.springvuegradle.team200.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Map;

@RepositoryRestResource
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    @Query("SELECT u FROM User u WHERE u.id = ?1")
    User getUserFromId(Long id);

    User findByIsGlobalAdminTrue();

    @Query("SELECT u FROM User u JOIN FETCH u.userEmails e WHERE e.address = ?1 AND e.user = u")
    User getUserFromEmail(String email);

    @Query("SELECT u FROM User u WHERE u.username = ?1")
    User getUserFromUsername(String username);

    @Query("SELECT u FROM User u WHERE u.firstName = ?1")
    User getUserFromFirstName(String firstName);

    Long deleteAllByIsGlobalAdminFalse();

    @Query(nativeQuery = true,
            value = "SELECT * FROM app_user u WHERE u.user_id IN " +
                    "(SELECT af.user_id FROM user_activity_followers af WHERE af.activity_id = ?1)")
    List<User> findFollowersByActivityId(Long activityId);

    @Query(nativeQuery = true,
            value = "SELECT * FROM app_user u WHERE u.user_id IN " +
                    "(SELECT af.user_id FROM user_activity_followers af WHERE af.activity_id = ?1)")
    Page<User> findFollowersByActivityId(Long activityId, Pageable pageable);

    @Query(nativeQuery = true,
            value = "SELECT * FROM app_user u WHERE u.user_id IN " +
                    "(SELECT ap.user_id FROM user_activity_participation ap WHERE ap.activity_id = ?1)")
    List<User> findParticipantsByActivityId(Long activityId);

    @Query(nativeQuery = true,
            value = "SELECT * FROM app_user u WHERE u.user_id IN " +
                    "(SELECT ap.user_id FROM user_activity_participation ap WHERE ap.activity_id = ?1)")
    Page<User> findParticipantsByActivityId(Long activityId, Pageable pageable);

    @Query(nativeQuery = true,
            value = "SELECT * FROM app_user u WHERE u.user_id IN " +
                    "(SELECT ao.user_id FROM user_activity_organisers ao WHERE ao.activity_id = ?1)")
    List<User> findOrganisersByActivityId(Long activityId);

    @Query(nativeQuery = true,
            value = "SELECT * FROM app_user u WHERE u.user_id IN " +
                    "(SELECT ao.user_id FROM user_activity_organisers ao WHERE ao.activity_id = ?1)")
    Page<User> findOrganisersByActivityId(Long activityId, Pageable pageable);

    @Query(value = "SELECT f.id FROM User u INNER JOIN u.activitiesFollowing f WHERE u.id = ?1")
    List<Long> findFollowingActivityIds(Long userId);

    @Query(value = "SELECT p.id FROM User u INNER JOIN u.activityParticipator p WHERE u.id = ?1")
    List<Long> findParticipatingActivityIds(Long userId);

    @Query(nativeQuery = true,
            value = "SELECT u.user_id as userId, u.first_name as firstName, u.last_name as lastName, e.address, all_users.role FROM " +
                    "(SELECT user_id, 'Organiser' as role FROM user_activity_organisers WHERE activity_id = ?1 " +
                    "UNION " +
                    "SELECT user_id, 'Follower' as role FROM user_activity_followers WHERE activity_id = ?1 " +
                    "UNION " +
                    "SELECT user_id, 'Participant' as role FROM user_activity_participation WHERE activity_id = ?1) all_users " +
                    "INNER JOIN app_user u ON u.user_id = all_users.user_id " +
                    "INNER JOIN app_user_email e ON u.user_id = e.user_id " +
                    "WHERE e.is_primary = 1",
            countQuery = "SELECT " +
                    "(select count(*) from user_activity_organisers o WHERE o.activity_id = ?1) +\n" +
                    "(select count(*) from user_activity_participation o WHERE o.activity_id = ?1) +\n" +
                    "(select count(*) from user_activity_followers o WHERE o.activity_id = ?1)")
    Page<Map<String, Object>> findUsersRelatedToActivityByActivityId(Long activityId, Pageable pageable);
}

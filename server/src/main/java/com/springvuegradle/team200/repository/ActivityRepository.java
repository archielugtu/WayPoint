package com.springvuegradle.team200.repository;

import com.springvuegradle.team200.dto.response.UserResponse;
import com.springvuegradle.team200.model.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RepositoryRestResource
public interface ActivityRepository extends JpaRepository<Activity, Long>, JpaSpecificationExecutor<Activity> {

    Optional<Activity> findByActivityName(String name);

    Activity findFirstByActivityName(String name);
    @Query(nativeQuery = true,
            value = "SELECT visibility FROM activity a WHERE a.activity_id = ?1")
    String findActivityVisibilityById(Long activityId);


    /**
     *  Finds the role a user has with a given activity. Role is returned as an int according to ActivityRole Enum
     */
    @Query(nativeQuery = true,
            value = "select \n" +
                    "(select count(*) * 4 from activity a WHERE a.user_id = ?1 AND a.activity_id = ?2) +\n" +
                    "(select count(*) * 3 from user_activity_organisers o WHERE o.user_id = ?1 AND o.activity_id = ?2) +\n" +
                    "(select count(*) * 2 from user_activity_participation o WHERE o.user_id = ?1 AND o.activity_id = ?2) +\n" +
                    "(select count(*) * 1 from user_activity_followers o WHERE o.user_id = ?1 AND o.activity_id = ?2) ")
    int findActivityRole(Long userId, Long activityId);

    @Query(nativeQuery = true,
            value = "SELECT * FROM activity a WHERE a.user_id = ?1 " +
                    "UNION " +
                    "SELECT * FROM activity a WHERE a.activity_id IN (SELECT af.activity_id FROM user_activity_followers af WHERE af.user_id = ?1) " +
                    "UNION " +
                    "SELECT * FROM activity a WHERE a.activity_id IN (SELECT ap.activity_id FROM user_activity_participation ap WHERE ap.user_id = ?1) " +
                    "UNION " +
                    "SELECT * FROM activity a WHERE a.activity_id IN (SELECT ao.activity_id FROM user_activity_organisers ao WHERE ao.user_id = ?1)")
    List<Activity> findAllActivitiesByUserId(Long userId);

    @Query(nativeQuery = true,
            value = "SELECT * FROM activity a WHERE a.user_id = ?1")
    List<Activity> findActivitiesCreatedByUserId(Long userId);

    @Query(value = "SELECT a FROM Activity a JOIN a.followers f WHERE a.id = ?1")
    List<Activity> findFollowersByActivityId(Long activityId);

    @Modifying
    @Query(nativeQuery = true,
            value = "DELETE FROM user_activity_followers  WHERE activity_id = ?1")
    void removeFollowersOfActivityId(Long activityId);

    @Modifying
    @Query(nativeQuery = true,
            value = "DELETE FROM user_activity_participation  WHERE activity_id = ?1")
    void removeParticipantsOfActivityId(Long activityId);

    @Modifying
    @Query(nativeQuery = true,
            value = "DELETE FROM user_activity_shared_users WHERE activity_id = ?1")
    void removeSharedUsersOfActivity(Long activityId);

    @Modifying
    @Query(nativeQuery = true,
            value = "DELETE FROM user_activity_organisers  WHERE activity_id = ?1")
    void removeOrganisersOfActivityId(Long activityId);

    @Query(nativeQuery = true,
            value = "SELECT count(*) FROM user_activity_participation  WHERE activity_id = ?1")
    int countParticipantsByActivityId(Long activityId);

    @Query(nativeQuery = true,
            value = "SELECT count(*) FROM user_activity_followers  WHERE activity_id = ?1")
    int countFollowersByActivityId(Long activityId);

    @Query(nativeQuery = true,
            value = "SELECT count(*) FROM user_activity_organisers  WHERE activity_id = ?1")
    int countOrganisersByActivityId(Long activityId);


    @Query("SELECT u FROM User u JOIN u.outcomeAnswers a JOIN a.outcomeQuestion q JOIN q.activity ac WHERE ac.id = ?1")
    Set<UserResponse> getUserWithOutcomeAnswers(Long activityId);
}

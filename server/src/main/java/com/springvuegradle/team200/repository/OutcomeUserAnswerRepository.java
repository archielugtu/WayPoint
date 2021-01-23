package com.springvuegradle.team200.repository;

import com.springvuegradle.team200.model.OutcomeQuestion;
import com.springvuegradle.team200.model.OutcomeUserAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Set;

@RepositoryRestResource
public interface OutcomeUserAnswerRepository extends JpaRepository<OutcomeUserAnswer, Long> {

    @Query(value =  "SELECT ua " +
                    "FROM OutcomeUserAnswer ua, OutcomeQuestion q, User u, Activity a " +
                    "WHERE u.id = ?1 " +
                    "AND a.id = ?2 " +
                    "AND q.activity = a " +
                    "AND ua.outcomeQuestion = q " +
                    "AND ua.user = u")
    Set<OutcomeUserAnswer> findOutcomeUserAnswerByUserAndActivity(Long userId, Long activityId);

    int countOutcomeUserAnswerByOutcomeQuestion(OutcomeQuestion question);

    void deleteAllByOutcomeQuestion(OutcomeQuestion question);
}

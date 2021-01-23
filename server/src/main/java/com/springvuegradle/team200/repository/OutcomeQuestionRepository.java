package com.springvuegradle.team200.repository;

import com.springvuegradle.team200.model.Activity;
import com.springvuegradle.team200.model.OutcomeQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface OutcomeQuestionRepository extends JpaRepository<OutcomeQuestion, Long> {

    List<OutcomeQuestion> findByActivity(Activity activity);

}

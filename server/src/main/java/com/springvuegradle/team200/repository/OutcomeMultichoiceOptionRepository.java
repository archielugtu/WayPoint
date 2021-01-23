package com.springvuegradle.team200.repository;

import com.springvuegradle.team200.model.OutcomeMultichoiceOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface OutcomeMultichoiceOptionRepository extends JpaRepository<OutcomeMultichoiceOption, Long> {

    void deleteByOutcomeQuestion_Id(Long questionId);
}

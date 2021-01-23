package com.springvuegradle.team200.repository;

import com.springvuegradle.team200.model.UserActivityHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface UserActivityHistoryRepository extends
        JpaRepository<UserActivityHistory, Long>,
        JpaSpecificationExecutor<UserActivityHistory> {
}

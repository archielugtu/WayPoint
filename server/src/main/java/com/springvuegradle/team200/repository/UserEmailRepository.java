package com.springvuegradle.team200.repository;

import com.springvuegradle.team200.model.UserEmail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource
public interface UserEmailRepository extends JpaRepository<UserEmail, Long> {

    @Query(value = "SELECT e FROM UserEmail e WHERE e.address = ?1")
    UserEmail getEmailFromAddress(String address);

    List<UserEmail> findByUser_Id(Long userId);

    Optional<UserEmail> findFirstByAddress(String address);

    @Query(value = "SELECT e.address FROM UserEmail e ORDER BY e.address ASC")
    List<String> getAllEmails();


}

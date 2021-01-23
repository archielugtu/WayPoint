package com.springvuegradle.team200.repository;

import com.springvuegradle.team200.model.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource
public interface HashtagRepository extends JpaRepository<Hashtag, Long> {
    Optional<Hashtag> findFirstByName(String name);

    @Query("SELECT h.name FROM Hashtag h order by h.activities.size desc")
    List<String> getHashtagsOrderedByActivities();
}

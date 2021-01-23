package com.springvuegradle.team200.repository;

import com.springvuegradle.team200.model.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;
import java.util.Optional;

@RepositoryRestResource
public interface PhotoRepository extends JpaRepository<Photo, Long> {
    Optional<Photo> findByIdAndActivity_Id(Long photoId, Long activityId);

    Optional<Photo> findByIsPrimaryTrueAndActivity_Id(Long activityId);

    List<Photo> findByActivity_Id(Long activityId);

    Optional<Photo> findByIsPrimaryTrueAndUser_Id(Long userId);

    Optional<Photo> findByIsPrimaryFalseAndUser_Id(Long userId);

    Optional<Photo> findByIsPrimaryAndUser_Id(boolean isPrimary, Long userId);

    Optional<Photo> findByIdAndUser_Id(Long photoId, Long userId);

    void deleteByIsPrimaryTrueAndUser_Id(Long userId);

    void deleteByIsPrimaryFalseAndUser_Id(Long userId);

    void deleteByIsPrimaryAndUser_Id(boolean isPrimary, Long userId);

}

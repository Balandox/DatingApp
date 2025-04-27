package org.suai.users.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.suai.users.model.entities.Interest;
import org.suai.users.model.entities.Photo;

import java.util.Optional;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {
    Optional<Photo> findByPositionAndProfileId(Integer position, Long profileId);

    void deleteByProfileId(Long profileId);
}

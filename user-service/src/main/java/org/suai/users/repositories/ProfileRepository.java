package org.suai.users.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.suai.users.model.entities.Profile;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
}

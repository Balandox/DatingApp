package org.suai.users.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.suai.users.model.entities.Profile;
import org.suai.users.model.entities.ProfileFact;
import org.suai.users.model.entities.pk.ProfileFactId;

import java.util.List;

public interface ProfileFactRepository extends JpaRepository<ProfileFact, ProfileFactId> {

    List<ProfileFact> findByProfile(Profile profile);

    void deleteByProfileId(Long profileId);
}

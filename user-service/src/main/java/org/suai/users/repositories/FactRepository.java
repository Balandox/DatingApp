package org.suai.users.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.suai.users.model.entities.Fact;
import org.suai.users.model.entities.Interest;
import org.suai.users.model.enums.UserFacts;

import java.util.Optional;

@Repository
public interface FactRepository extends JpaRepository<Fact, Long> {

    Optional<Fact> findByName(UserFacts name);
}

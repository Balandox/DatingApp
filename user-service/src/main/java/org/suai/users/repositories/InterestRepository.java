package org.suai.users.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.suai.users.model.entities.Interest;
import org.suai.users.model.enums.UserInterest;

import java.util.Optional;

@Repository
public interface InterestRepository extends JpaRepository<Interest, Long> {

    Optional<Interest> findByName(UserInterest name);
}

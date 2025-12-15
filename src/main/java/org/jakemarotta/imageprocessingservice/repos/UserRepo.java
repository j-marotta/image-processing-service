package org.jakemarotta.imageprocessingservice.repos;

import org.jakemarotta.imageprocessingservice.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
}

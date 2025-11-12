package dev.gagnon.bfpcapi.data.repository;

import dev.gagnon.bfpcapi.data.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String principal);

    boolean existsByEmail(String email);
}

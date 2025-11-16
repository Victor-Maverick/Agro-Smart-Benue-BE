package dev.gagnon.bfpcapi.data.repository;

import dev.gagnon.bfpcapi.data.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findByEmail(String email);
    boolean existsByEmail(String email);
}

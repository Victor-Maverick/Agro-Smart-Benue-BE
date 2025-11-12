package dev.gagnon.bfpcapi.data.repository;

import dev.gagnon.bfpcapi.data.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}

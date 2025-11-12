package dev.gagnon.bfpcapi.data.repository;

import dev.gagnon.bfpcapi.data.model.Farm;
import dev.gagnon.bfpcapi.data.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FarmRepository extends JpaRepository<Farm, Long> {
    List<Farm> findByUser(User user);
    List<Farm> findByUserId(Long userId);
}
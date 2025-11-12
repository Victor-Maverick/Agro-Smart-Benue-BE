package dev.gagnon.bfpcapi.data.repository;

import dev.gagnon.bfpcapi.data.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    List<Location> findByLga(String lga);
    List<Location> findByState(String state);
    Optional<Location> findByNameAndLga(String name, String lga);
    boolean existsByNameAndLga(String name, String lga);
}
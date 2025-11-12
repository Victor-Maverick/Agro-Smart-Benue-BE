package dev.gagnon.bfpcapi.data.repository;

import dev.gagnon.bfpcapi.data.model.Market;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MarketRepository extends JpaRepository<Market, Long> {
    List<Market> findByLga(String lga);
    List<Market> findByState(String state);
    Optional<Market> findByNameAndLga(String name, String lga);
    boolean existsByNameAndLga(String name, String lga);
}
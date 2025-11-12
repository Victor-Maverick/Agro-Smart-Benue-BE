package dev.gagnon.bfpcapi.data.repository;

import dev.gagnon.bfpcapi.data.model.ProductDemand;
import dev.gagnon.bfpcapi.data.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductDemandRepository extends JpaRepository<ProductDemand, Long> {
    List<ProductDemand> findByBuyer(User buyer);
    List<ProductDemand> findByIsActiveTrue();
    List<ProductDemand> findByProductNameContainingIgnoreCase(String productName);
    List<ProductDemand> findByLocationContainingIgnoreCase(String location);
}
package dev.gagnon.bfpcapi.data.repository;

import dev.gagnon.bfpcapi.data.model.Product;
import dev.gagnon.bfpcapi.data.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("select p from Product p where p.farmer.email=:email and p.name=:name and p.quantityCategory=:quantityCategory")
    List<Product> findAllByUserAndName(String email, String name, String quantityCategory);
    
    List<Product> findByFarmer(User farmer);
    
    List<Product> findByIsAvailableTrue();
    
    List<Product> findByFarmerAndIsAvailableTrue(User farmer);
}

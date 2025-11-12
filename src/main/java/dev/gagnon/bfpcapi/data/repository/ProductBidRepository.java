package dev.gagnon.bfpcapi.data.repository;

import dev.gagnon.bfpcapi.data.model.Product;
import dev.gagnon.bfpcapi.data.model.ProductBid;
import dev.gagnon.bfpcapi.data.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductBidRepository extends JpaRepository<ProductBid, Long> {
    List<ProductBid> findByProduct(Product product);
    List<ProductBid> findByBidder(User bidder);
    List<ProductBid> findByProductAndStatus(Product product, ProductBid.BidStatus status);
    List<ProductBid> findByStatus(ProductBid.BidStatus status);
}
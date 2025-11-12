package dev.gagnon.bfpcapi.data.repository;

import dev.gagnon.bfpcapi.data.model.Crop;
import dev.gagnon.bfpcapi.data.model.MarketPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MarketPriceRepository extends JpaRepository<MarketPrice, Long> {
    List<MarketPrice> findByCrop(Crop crop);
    List<MarketPrice> findByCropAndState(Crop crop, String state);
    List<MarketPrice> findByMarket(String market);
    
    @Query("SELECT mp FROM MarketPrice mp WHERE mp.priceDate >= :startDate ORDER BY mp.priceDate DESC")
    List<MarketPrice> findRecentPrices(@Param("startDate") LocalDate startDate);
    
    @Query("SELECT mp FROM MarketPrice mp WHERE mp.crop.id = :cropId AND mp.priceDate >= :startDate ORDER BY mp.priceDate DESC")
    List<MarketPrice> findRecentPricesByCrop(@Param("cropId") Long cropId, @Param("startDate") LocalDate startDate);
    
    @Query("SELECT mp FROM MarketPrice mp WHERE mp.crop.id = :cropId AND mp.market = :market AND mp.state = :state")
    Optional<MarketPrice> findByCropAndMarketAndState(@Param("cropId") Long cropId, @Param("market") String market, @Param("state") String state);
    
    @Query("SELECT mp FROM MarketPrice mp WHERE mp.crop.id = :cropId ORDER BY mp.priceDate DESC")
    List<MarketPrice> findByCropIdOrderByPriceDateDesc(@Param("cropId") Long cropId);
}
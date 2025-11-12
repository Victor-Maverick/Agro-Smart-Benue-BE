package dev.gagnon.bfpcapi.service;

import dev.gagnon.bfpcapi.data.model.MarketPrice;
import dev.gagnon.bfpcapi.dto.request.MarketPriceRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface MarketPriceService {
    MarketPrice createMarketPrice(MarketPriceRequest request);
    List<MarketPrice> getRecentPrices();
    Page<MarketPrice> getRecentPricesPaginated(Pageable pageable);
    List<MarketPrice> getPricesByCrop(Long cropId);
    List<MarketPrice> getPricesByMarket(String market);
    MarketPrice updateMarketPrice(Long priceId, MarketPriceRequest request);
    MarketPrice updateOrCreateMarketPrice(MarketPriceRequest request);
    List<MarketPrice> getPricesByCropGroupedByMarket(Long cropId);
    Map<String, Object> getMarketPriceStatistics();
    void deleteMarketPrice(Long priceId);
}
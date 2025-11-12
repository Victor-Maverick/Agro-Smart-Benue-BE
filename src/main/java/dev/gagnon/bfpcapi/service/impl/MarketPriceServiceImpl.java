package dev.gagnon.bfpcapi.service.impl;

import dev.gagnon.bfpcapi.data.model.Crop;
import dev.gagnon.bfpcapi.data.model.MarketPrice;
import dev.gagnon.bfpcapi.data.repository.MarketPriceRepository;
import dev.gagnon.bfpcapi.dto.request.MarketPriceRequest;
import dev.gagnon.bfpcapi.exception.BusinessException;
import dev.gagnon.bfpcapi.service.CropService;
import dev.gagnon.bfpcapi.service.MarketPriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MarketPriceServiceImpl implements MarketPriceService {
    
    private final MarketPriceRepository marketPriceRepository;
    private final CropService cropService;

    @Override
    public MarketPrice createMarketPrice(MarketPriceRequest request) {
        Crop crop = cropService.getCropById(request.getCropId());
        MarketPrice marketPrice = MarketPrice.builder()
                .crop(crop)
                .market(request.getMarket())
                .state(request.getState())
                .lga(request.getLga())
                .price(request.getPrice())
                .unit(request.getUnit())
                .priceDate(request.getPriceDate())
                .quality(request.getQuality())
                .build();
        return marketPriceRepository.save(marketPrice);
    }

    @Override
    public List<MarketPrice> getRecentPrices() {
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        return marketPriceRepository.findRecentPrices(thirtyDaysAgo);
    }

    @Override
    public List<MarketPrice> getPricesByCrop(Long cropId) {
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        return marketPriceRepository.findRecentPricesByCrop(cropId, thirtyDaysAgo);
    }

    @Override
    public List<MarketPrice> getPricesByMarket(String market) {
        return marketPriceRepository.findByMarket(market);
    }

    @Override
    public MarketPrice updateMarketPrice(Long priceId, MarketPriceRequest request) {
        MarketPrice marketPrice = marketPriceRepository.findById(priceId)
                .orElseThrow(() -> new BusinessException("Market price not found"));

        Crop crop = cropService.getCropById(request.getCropId());
        
        marketPrice.setCrop(crop);
        marketPrice.setMarket(request.getMarket());
        marketPrice.setState(request.getState());
        marketPrice.setLga(request.getLga());
        marketPrice.setPrice(request.getPrice());
        marketPrice.setUnit(request.getUnit());
        marketPrice.setPriceDate(request.getPriceDate());
        marketPrice.setQuality(request.getQuality());

        return marketPriceRepository.save(marketPrice);
    }

    @Override
    public MarketPrice updateOrCreateMarketPrice(MarketPriceRequest request) {
        Crop crop = cropService.getCropById(request.getCropId());
        
        // Try to find existing price for this crop, market, and state
        Optional<MarketPrice> existingPrice = marketPriceRepository
                .findByCropAndMarketAndState(request.getCropId(), request.getMarket(), request.getState());
        
        MarketPrice marketPrice;
        if (existingPrice.isPresent()) {
            // Update existing price
            marketPrice = existingPrice.get();
            marketPrice.setPrice(request.getPrice());
            marketPrice.setUnit(request.getUnit());
            marketPrice.setPriceDate(request.getPriceDate());
            marketPrice.setQuality(request.getQuality());
            marketPrice.setLga(request.getLga());
        } else {
            // Create new price entry
            marketPrice = MarketPrice.builder()
                    .crop(crop)
                    .market(request.getMarket())
                    .state(request.getState())
                    .lga(request.getLga())
                    .price(request.getPrice())
                    .unit(request.getUnit())
                    .priceDate(request.getPriceDate())
                    .quality(request.getQuality())
                    .build();
        }
        
        return marketPriceRepository.save(marketPrice);
    }

    @Override
    public List<MarketPrice> getPricesByCropGroupedByMarket(Long cropId) {
        return marketPriceRepository.findByCropIdOrderByPriceDateDesc(cropId);
    }

    @Override
    public Page<MarketPrice> getRecentPricesPaginated(Pageable pageable) {
        return marketPriceRepository.findAll(pageable);
    }

    @Override
    public Map<String, Object> getMarketPriceStatistics() {
        List<MarketPrice> allPrices = marketPriceRepository.findAll();
        
        // Group by crop and calculate average prices
        Map<String, Double> cropPriceAverages = allPrices.stream()
                .collect(Collectors.groupingBy(
                        mp -> mp.getCrop().getName(),
                        Collectors.averagingDouble(mp -> mp.getPrice().doubleValue())
                ));
        
        // Convert to list of maps for pie chart
        List<Map<String, Object>> priceDistribution = cropPriceAverages.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("name", entry.getKey());
                    item.put("value", Math.round(entry.getValue()));
                    return item;
                })
                .sorted((a, b) -> Double.compare((Double)b.get("value"), (Double)a.get("value")))
                .limit(10)
                .collect(Collectors.toList());
        
        // Market count
        Long marketCount = allPrices.stream()
                .map(MarketPrice::getMarket)
                .distinct()
                .count();
        
        // Total crops tracked
        long cropCount = allPrices.stream()
                .map(mp -> mp.getCrop().getId())
                .distinct()
                .count();
        
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("priceDistribution", priceDistribution);
        statistics.put("totalMarkets", marketCount);
        statistics.put("totalCrops", cropCount);
        statistics.put("totalPriceEntries", allPrices.size());
        
        return statistics;
    }

    @Override
    public void deleteMarketPrice(Long priceId) {
        MarketPrice marketPrice = marketPriceRepository.findById(priceId)
                .orElseThrow(() -> new BusinessException("Market price not found"));
        marketPriceRepository.delete(marketPrice);
    }
}
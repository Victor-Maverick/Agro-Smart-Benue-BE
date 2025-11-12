package dev.gagnon.bfpcapi.controller;

import dev.gagnon.bfpcapi.data.model.MarketPrice;
import dev.gagnon.bfpcapi.dto.request.MarketPriceRequest;
import dev.gagnon.bfpcapi.dto.response.BfpcApiResponse;
import dev.gagnon.bfpcapi.service.MarketPriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/market-prices")
@RequiredArgsConstructor
public class MarketPriceController {
    
    private final MarketPriceService marketPriceService;

    @PostMapping
    public ResponseEntity<BfpcApiResponse<MarketPrice>> createMarketPrice(@RequestBody MarketPriceRequest request) {
        MarketPrice marketPrice = marketPriceService.createMarketPrice(request);
        return ResponseEntity.ok(new BfpcApiResponse<>(true, marketPrice));
    }

    @GetMapping("/recent")
    public ResponseEntity<BfpcApiResponse<List<MarketPrice>>> getRecentPrices() {
        List<MarketPrice> prices = marketPriceService.getRecentPrices();
        return ResponseEntity.ok(new BfpcApiResponse<>(true, prices));
    }

    @GetMapping("/crop/{cropId}")
    public ResponseEntity<BfpcApiResponse<List<MarketPrice>>> getPricesByCrop(@PathVariable Long cropId) {
        List<MarketPrice> prices = marketPriceService.getPricesByCrop(cropId);
        return ResponseEntity.ok(new BfpcApiResponse<>(true, prices));
    }

    @GetMapping("/market/{market}")
    public ResponseEntity<BfpcApiResponse<List<MarketPrice>>> getPricesByMarket(@PathVariable String market) {
        List<MarketPrice> prices = marketPriceService.getPricesByMarket(market);
        return ResponseEntity.ok(new BfpcApiResponse<>(true, prices));
    }

    @PutMapping("/{priceId}")
    public ResponseEntity<BfpcApiResponse<MarketPrice>> updateMarketPrice(@PathVariable Long priceId, @RequestBody MarketPriceRequest request) {
        MarketPrice marketPrice = marketPriceService.updateMarketPrice(priceId, request);
        return ResponseEntity.ok(new BfpcApiResponse<>(true, marketPrice));
    }

    @PostMapping("/update-or-create")
    public ResponseEntity<BfpcApiResponse<MarketPrice>> updateOrCreateMarketPrice(@RequestBody MarketPriceRequest request) {
        MarketPrice marketPrice = marketPriceService.updateOrCreateMarketPrice(request);
        return ResponseEntity.ok(new BfpcApiResponse<>(true, marketPrice));
    }

    @GetMapping("/crop/{cropId}/by-market")
    public ResponseEntity<BfpcApiResponse<List<MarketPrice>>> getPricesByCropGroupedByMarket(@PathVariable Long cropId) {
        List<MarketPrice> prices = marketPriceService.getPricesByCropGroupedByMarket(cropId);
        return ResponseEntity.ok(new BfpcApiResponse<>(true, prices));
    }

    @GetMapping("/paginated")
    public ResponseEntity<BfpcApiResponse<Page<MarketPrice>>> getRecentPricesPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(defaultValue = "priceDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<MarketPrice> prices = marketPriceService.getRecentPricesPaginated(pageable);
        return ResponseEntity.ok(new BfpcApiResponse<>(true, prices));
    }

    @GetMapping("/statistics")
    public ResponseEntity<BfpcApiResponse<Map<String, Object>>> getStatistics() {
        Map<String, Object> statistics = marketPriceService.getMarketPriceStatistics();
        return ResponseEntity.ok(new BfpcApiResponse<>(true, statistics));
    }

    @DeleteMapping("/{priceId}")
    public ResponseEntity<BfpcApiResponse<Void>> deleteMarketPrice(@PathVariable Long priceId) {
        marketPriceService.deleteMarketPrice(priceId);
        return ResponseEntity.ok(new BfpcApiResponse<>(true, null));
    }
}
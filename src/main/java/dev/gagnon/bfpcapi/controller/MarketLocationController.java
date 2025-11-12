package dev.gagnon.bfpcapi.controller;

import dev.gagnon.bfpcapi.data.model.Market;
import dev.gagnon.bfpcapi.data.model.Location;
import dev.gagnon.bfpcapi.data.repository.MarketRepository;
import dev.gagnon.bfpcapi.data.repository.LocationRepository;
import dev.gagnon.bfpcapi.dto.response.BfpcApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MarketLocationController {
    
    private final MarketRepository marketRepository;
    private final LocationRepository locationRepository;

    // Market endpoints
    @GetMapping("/markets")
    public ResponseEntity<BfpcApiResponse<List<Market>>> getAllMarkets() {
        List<Market> markets = marketRepository.findAll();
        return ResponseEntity.ok(new BfpcApiResponse<>(true, markets));
    }

    @GetMapping("/markets/by-lga")
    public ResponseEntity<BfpcApiResponse<List<Market>>> getMarketsByLga(@RequestParam String lga) {
        List<Market> markets = marketRepository.findByLga(lga);
        return ResponseEntity.ok(new BfpcApiResponse<>(true, markets));
    }

    @PostMapping("/markets")
    public ResponseEntity<BfpcApiResponse<Market>> createMarket(@RequestBody Market market) {
        if (marketRepository.existsByNameAndLga(market.getName(), market.getLga())) {
            return ResponseEntity.badRequest().body(new BfpcApiResponse<>(false, null));
        }
        Market savedMarket = marketRepository.save(market);
        return ResponseEntity.ok(new BfpcApiResponse<>(true, savedMarket));
    }

    // Location endpoints
    @GetMapping("/locations")
    public ResponseEntity<BfpcApiResponse<List<Location>>> getAllLocations() {
        List<Location> locations = locationRepository.findAll();
        return ResponseEntity.ok(new BfpcApiResponse<>(true, locations));
    }

    @GetMapping("/locations/by-lga")
    public ResponseEntity<BfpcApiResponse<List<Location>>> getLocationsByLga(@RequestParam String lga) {
        List<Location> locations = locationRepository.findByLga(lga);
        return ResponseEntity.ok(new BfpcApiResponse<>(true, locations));
    }

    @PostMapping("/locations")
    public ResponseEntity<BfpcApiResponse<Location>> createLocation(@RequestBody Location location) {
        if (locationRepository.existsByNameAndLga(location.getName(), location.getLga())) {
            return ResponseEntity.badRequest().body(new BfpcApiResponse<>(false, null));
        }
        Location savedLocation = locationRepository.save(location);
        return ResponseEntity.ok(new BfpcApiResponse<>(true, savedLocation));
    }
}
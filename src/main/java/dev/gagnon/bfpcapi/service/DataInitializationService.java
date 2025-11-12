package dev.gagnon.bfpcapi.service;

import dev.gagnon.bfpcapi.data.model.Crop;
import dev.gagnon.bfpcapi.data.model.MarketPrice;
import dev.gagnon.bfpcapi.data.repository.CropRepository;
import dev.gagnon.bfpcapi.data.repository.MarketPriceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataInitializationService implements CommandLineRunner {
    
    private final CropRepository cropRepository;
    private final MarketPriceRepository marketPriceRepository;

    @Override
    public void run(String... args) throws Exception {
        initializeCrops();
        initializeMarketPrices();
    }

    private void initializeCrops() {
        if (cropRepository.count() == 0) {
            log.info("Initializing default crops...");
            
            List<Crop> defaultCrops = Arrays.asList(
                // Grains
                Crop.builder()
                    .name("Rice")
                    .category("grains")
                    .description("Staple grain crop, widely cultivated in Benue State")
                    .plantingSeason("May-July")
                    .harvestSeason("October-December")
                    .growthPeriodDays(120)
                    .build(),
                
                Crop.builder()
                    .name("Maize")
                    .category("grains")
                    .description("Corn crop, important for food security")
                    .plantingSeason("April-June")
                    .harvestSeason("August-October")
                    .growthPeriodDays(90)
                    .build(),
                
                Crop.builder()
                    .name("Sorghum")
                    .category("grains")
                    .description("Drought-resistant grain crop")
                    .plantingSeason("May-July")
                    .harvestSeason("October-December")
                    .growthPeriodDays(120)
                    .build(),
                
                Crop.builder()
                    .name("Millet")
                    .category("grains")
                    .description("Hardy grain crop suitable for dry conditions")
                    .plantingSeason("May-July")
                    .harvestSeason("September-November")
                    .growthPeriodDays(90)
                    .build(),
                
                // Tubers
                Crop.builder()
                    .name("Yam")
                    .category("tubers")
                    .description("Major tuber crop, Benue is a leading producer")
                    .plantingSeason("March-May")
                    .harvestSeason("October-December")
                    .growthPeriodDays(240)
                    .build(),
                
                Crop.builder()
                    .name("Cassava")
                    .category("tubers")
                    .description("Drought-tolerant root crop")
                    .plantingSeason("March-July")
                    .harvestSeason("Year-round")
                    .growthPeriodDays(365)
                    .build(),
                
                Crop.builder()
                    .name("Sweet Potato")
                    .category("tubers")
                    .description("Nutritious root vegetable")
                    .plantingSeason("April-July")
                    .harvestSeason("August-December")
                    .growthPeriodDays(120)
                    .build(),
                
                // Legumes
                Crop.builder()
                    .name("Soybean")
                    .category("legumes")
                    .description("Protein-rich legume crop")
                    .plantingSeason("May-July")
                    .harvestSeason("September-November")
                    .growthPeriodDays(100)
                    .build(),
                
                Crop.builder()
                    .name("Groundnut")
                    .category("legumes")
                    .description("Peanut crop, good source of oil and protein")
                    .plantingSeason("May-July")
                    .harvestSeason("September-November")
                    .growthPeriodDays(90)
                    .build(),
                
                Crop.builder()
                    .name("Cowpea")
                    .category("legumes")
                    .description("Black-eyed pea, important protein source")
                    .plantingSeason("May-August")
                    .harvestSeason("August-December")
                    .growthPeriodDays(75)
                    .build(),
                
                // Cash Crops
                Crop.builder()
                    .name("Sesame")
                    .category("cash_crops")
                    .description("Oil seed crop with export potential")
                    .plantingSeason("May-July")
                    .harvestSeason("September-November")
                    .growthPeriodDays(90)
                    .build(),
                
                Crop.builder()
                    .name("Cotton")
                    .category("cash_crops")
                    .description("Fiber crop for textile industry")
                    .plantingSeason("May-July")
                    .harvestSeason("October-December")
                    .growthPeriodDays(150)
                    .build(),
                
                // Vegetables
                Crop.builder()
                    .name("Tomato")
                    .category("vegetables")
                    .description("Popular vegetable crop")
                    .plantingSeason("Year-round")
                    .harvestSeason("Year-round")
                    .growthPeriodDays(75)
                    .build(),
                
                Crop.builder()
                    .name("Pepper")
                    .category("vegetables")
                    .description("Spicy vegetable crop")
                    .plantingSeason("Year-round")
                    .harvestSeason("Year-round")
                    .growthPeriodDays(90)
                    .build(),
                
                Crop.builder()
                    .name("Onion")
                    .category("vegetables")
                    .description("Essential vegetable crop")
                    .plantingSeason("October-December")
                    .harvestSeason("March-May")
                    .growthPeriodDays(120)
                    .build()
            );
            
            cropRepository.saveAll(defaultCrops);
            log.info("Successfully initialized {} default crops", defaultCrops.size());
        } else {
            log.info("Crops already exist in database, skipping initialization");
        }
    }

    private void initializeMarketPrices() {
        if (marketPriceRepository.count() == 0) {
            log.info("Initializing default market prices...");
            
            // Get some crops for price initialization
            List<Crop> crops = cropRepository.findAll();
            if (crops.isEmpty()) {
                log.warn("No crops found, skipping market price initialization");
                return;
            }

            List<MarketPrice> defaultPrices = Arrays.asList(
                // Rice prices across different markets
                createMarketPrice(findCropByName(crops, "Rice"), "Makurdi Main Market", "Benue", "Makurdi", new BigDecimal("45000"), "bag (50kg)", "medium"),
                createMarketPrice(findCropByName(crops, "Rice"), "Gboko Market", "Benue", "Gboko", new BigDecimal("43000"), "bag (50kg)", "medium"),
                createMarketPrice(findCropByName(crops, "Rice"), "Otukpo Market", "Benue", "Otukpo", new BigDecimal("46000"), "bag (50kg)", "high"),
                
                // Yam prices
                createMarketPrice(findCropByName(crops, "Yam"), "Makurdi Main Market", "Benue", "Makurdi", new BigDecimal("25000"), "tuber", "medium"),
                createMarketPrice(findCropByName(crops, "Yam"), "Gboko Market", "Benue", "Gboko", new BigDecimal("23000"), "tuber", "medium"),
                createMarketPrice(findCropByName(crops, "Yam"), "Katsina-Ala Market", "Benue", "Katsina-Ala", new BigDecimal("22000"), "tuber", "high"),
                
                // Cassava prices
                createMarketPrice(findCropByName(crops, "Cassava"), "Makurdi Main Market", "Benue", "Makurdi", new BigDecimal("15000"), "bag (50kg)", "medium"),
                createMarketPrice(findCropByName(crops, "Cassava"), "Otukpo Market", "Benue", "Otukpo", new BigDecimal("14500"), "bag (50kg)", "medium"),
                createMarketPrice(findCropByName(crops, "Cassava"), "Vandeikya Market", "Benue", "Vandeikya", new BigDecimal("16000"), "bag (50kg)", "high"),
                
                // Maize prices
                createMarketPrice(findCropByName(crops, "Maize"), "Makurdi Main Market", "Benue", "Makurdi", new BigDecimal("35000"), "bag (50kg)", "medium"),
                createMarketPrice(findCropByName(crops, "Maize"), "Katsina-Ala Market", "Benue", "Katsina-Ala", new BigDecimal("33000"), "bag (50kg)", "medium"),
                createMarketPrice(findCropByName(crops, "Maize"), "Aliade Market", "Benue", "Guma", new BigDecimal("34000"), "bag (50kg)", "high"),
                
                // Soybean prices
                createMarketPrice(findCropByName(crops, "Soybean"), "Makurdi Main Market", "Benue", "Makurdi", new BigDecimal("55000"), "bag (50kg)", "medium"),
                createMarketPrice(findCropByName(crops, "Soybean"), "Gboko Market", "Benue", "Gboko", new BigDecimal("54000"), "bag (50kg)", "medium"),
                createMarketPrice(findCropByName(crops, "Soybean"), "Oju Market", "Benue", "Oju", new BigDecimal("56000"), "bag (50kg)", "high")
            );
            
            // Filter out null prices (in case some crops don't exist)
            List<MarketPrice> validPrices = defaultPrices.stream()
                    .filter(price -> price != null)
                    .toList();
            
            if (!validPrices.isEmpty()) {
                marketPriceRepository.saveAll(validPrices);
                log.info("Successfully initialized {} default market prices", validPrices.size());
            }
        } else {
            log.info("Market prices already exist in database, skipping initialization");
        }
    }

    private Crop findCropByName(List<Crop> crops, String name) {
        return crops.stream()
                .filter(crop -> crop.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    private MarketPrice createMarketPrice(Crop crop, String market, String state, String lga, 
                                        BigDecimal price, String unit, String quality) {
        if (crop == null) {
            return null;
        }
        
        return MarketPrice.builder()
                .crop(crop)
                .market(market)
                .state(state)
                .lga(lga)
                .price(price)
                .unit(unit)
                .priceDate(LocalDate.now())
                .quality(quality)
                .build();
    }
}
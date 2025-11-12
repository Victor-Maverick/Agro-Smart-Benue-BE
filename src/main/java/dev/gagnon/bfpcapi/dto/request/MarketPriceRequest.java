package dev.gagnon.bfpcapi.dto.request;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class MarketPriceRequest {
    private Long cropId;
    private String market;
    private String state;
    private String lga;
    private BigDecimal price;
    private String unit;
    private LocalDate priceDate;
    private String quality;
}
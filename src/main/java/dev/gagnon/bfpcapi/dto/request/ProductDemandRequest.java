package dev.gagnon.bfpcapi.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDemandRequest {
    private String productName;
    private String description;
    private BigDecimal offerPrice;
    private Long quantity;
    private String quantityCategory; // bag, tubers, rubber, ton
    private String location;
    private String phoneContact;
    private Long cropId;
}
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
public class ProductBidRequest {
    private Long productId;
    private BigDecimal bidPrice;
    private Long quantity;
    private String message;
    private String phoneContact;
    private String location;
}
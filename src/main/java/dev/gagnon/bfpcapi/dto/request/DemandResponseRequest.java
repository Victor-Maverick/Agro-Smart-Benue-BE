package dev.gagnon.bfpcapi.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DemandResponseRequest {
    private Long demandId;
    private String productName;
    private String description;
    private BigDecimal offerPrice;
    private Long availableQuantity;
    private String quantityCategory;
    private String location;
    private String phoneContact;
    private MultipartFile image; // optional
}
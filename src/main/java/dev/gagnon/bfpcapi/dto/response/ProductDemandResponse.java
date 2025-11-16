package dev.gagnon.bfpcapi.dto.response;

import dev.gagnon.bfpcapi.data.model.ProductDemand;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductDemandResponse {
    private Long id;
    private String productName;
    private String description;
    private Double offerPrice;
    private Long quantity;
    private String quantityCategory; // bag, tubers, rubber, ton
    private String location;
    private String phoneContact;
    private LocalDateTime createdAt;
    private String phone;
    private String buyerName;
    public ProductDemandResponse(ProductDemand demand){
        this.id = demand.getId();
        this.productName = demand.getProductName();
        this.description = demand.getDescription();
        this.offerPrice = demand.getOfferPrice().doubleValue();
        this.quantity = demand.getQuantity();
        this.quantityCategory = demand.getQuantityCategory();
        this.location = demand.getLocation();
        this.phoneContact = demand.getPhoneContact();
        this.createdAt = demand.getCreatedAt();
        this.buyerName = demand.getBuyer().getFirstName() + " " + demand.getBuyer().getLastName();
    }

}

package dev.gagnon.bfpcapi.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import dev.gagnon.bfpcapi.data.model.Crop;
import dev.gagnon.bfpcapi.data.model.Product;
import dev.gagnon.bfpcapi.data.model.User;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private Double unitPrice;
    private Long quantity;
    private String quantityCategory;
    private String location;
    private boolean isAvailable;
    private String imageUrl;
    private String farmerName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ProductResponse(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.unitPrice = product.getUnitPrice().doubleValue();
        this.quantity = product.getQuantity();
        this.quantityCategory = product.getQuantityCategory();
        this.location = product.getLocation();
        this.isAvailable = product.isAvailable();
        this.createdAt = product.getCreatedAt();
        this.updatedAt = product.getUpdatedAt();
        this.imageUrl = product.getImageUrl();
        this.farmerName = product.getFarmer().getFirstName()
                + " " + product.getFarmer().getLastName();
    }
}

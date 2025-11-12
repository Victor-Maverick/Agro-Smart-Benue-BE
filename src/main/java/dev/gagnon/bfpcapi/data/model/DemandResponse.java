package dev.gagnon.bfpcapi.data.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "demand_responses")
public class DemandResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String productName;
    private String description;
    private BigDecimal offerPrice;
    private Long availableQuantity;
    private String quantityCategory;
    private String location;
    private String phoneContact;
    private String imageUrl;
    
    @Enumerated(EnumType.STRING)
    private ResponseStatus status;
    
    @ManyToOne
    @JoinColumn(name = "demand_id")
    private ProductDemand demand;
    
    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private User supplier;
    
    @Setter(AccessLevel.NONE)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime createdAt;
    
    @Setter(AccessLevel.NONE)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime updatedAt;

    @PrePersist
    private void onCreate() {
        createdAt = LocalDateTime.now();
        status = ResponseStatus.PENDING;
    }

    @PreUpdate
    private void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum ResponseStatus {
        PENDING,
        ACCEPTED,
        REJECTED,
        CANCELLED
    }
}
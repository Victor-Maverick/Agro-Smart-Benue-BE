package dev.gagnon.bfpcapi.data.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "farm_crops")
public class FarmCrop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "farm_id", nullable = false)
    private Farm farm;

    @ManyToOne
    @JoinColumn(name = "crop_id", nullable = false)
    private Crop crop;

    private Double areaAllocated; // in hectares
    private LocalDate plantingDate;
    private LocalDate expectedHarvestDate;
    private String status; // planted, growing, harvested
    private Double expectedYield; // in tons
    private Double actualYield; // in tons

    @Setter(AccessLevel.NONE)
    private LocalDateTime createdAt;
    
    @Setter(AccessLevel.NONE)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
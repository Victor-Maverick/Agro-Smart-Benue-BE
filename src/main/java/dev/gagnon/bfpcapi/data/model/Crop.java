package dev.gagnon.bfpcapi.data.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "crops")
public class Crop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;
    
    private String category; // grains, tubers, vegetables, etc.
    private String description;
    private String plantingSeason;
    private String harvestSeason;
    private Integer growthPeriodDays;
    private String imageUrl;

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
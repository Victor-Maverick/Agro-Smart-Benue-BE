package dev.gagnon.bfpcapi.data.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "farms")
public class Farm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String farmName;
    
    private String location;
    private String coordinates; // GPS coordinates
    private Double size; // in hectares
    private String soilType;
    private String description;
    
    @OneToMany(mappedBy = "farm", cascade = CascadeType.ALL)
    private List<FarmCrop> farmCrops;

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
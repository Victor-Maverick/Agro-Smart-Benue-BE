package dev.gagnon.bfpcapi.data.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class CropTip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    
    @Column(length = 5000)
    private String description;
    
    @ElementCollection
    @CollectionTable(name = "crop_tip_images", joinColumns = @JoinColumn(name = "crop_tip_id"))
    @Column(name = "image_url")
    private List<String> imageUrls = new ArrayList<>();
    
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

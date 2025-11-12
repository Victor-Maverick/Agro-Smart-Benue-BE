package dev.gagnon.bfpcapi.data.model;

import dev.gagnon.bfpcapi.data.constants.EventMode;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private LocalDateTime eventDate;

    private LocalDateTime endDate;
    private EventMode eventMode;
    private String location;
    private String organizer;
    private String eventType;
    private String targetAudience;
    private String imageUrl;
    private String registrationUrl;
    private Integer maxParticipants;
    private boolean isActive = true;

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
package dev.gagnon.bfpcapi.dto.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class EventRequest {
    private String title;
    private String description;
    private String eventDate;
    private String endDate;
    private String location;
    private String organizer;
    private String eventMode;
    private String eventType;
    private String targetAudience;
    private MultipartFile image;
    private String meetingLink;
    private Integer maxParticipants;
}
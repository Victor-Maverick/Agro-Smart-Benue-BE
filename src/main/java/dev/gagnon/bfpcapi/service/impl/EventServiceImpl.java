package dev.gagnon.bfpcapi.service.impl;

import com.cloudinary.Cloudinary;
import dev.gagnon.bfpcapi.data.constants.EventMode;
import dev.gagnon.bfpcapi.data.model.Event;
import dev.gagnon.bfpcapi.data.repository.EventRepository;
import dev.gagnon.bfpcapi.dto.request.EventRequest;
import dev.gagnon.bfpcapi.exception.BusinessException;
import dev.gagnon.bfpcapi.service.EventService;
import dev.gagnon.bfpcapi.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static dev.gagnon.bfpcapi.utils.ServiceUtils.getMediaUrl;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final Cloudinary cloudinary;
    private final EventRepository eventRepository;
    private final NotificationService notificationService;
    private final dev.gagnon.bfpcapi.data.repository.UserRepository userRepository;

    private LocalDateTime parseIsoDateTime(String dateTimeString) {
        // Parse ISO 8601 format with 'Z' timezone (e.g., "2025-11-13T23:27:00.000Z")
        Instant instant = Instant.parse(dateTimeString);
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    @Override
    public Event createEvent(EventRequest request) {
        String imageUrl = null;
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            imageUrl = getMediaUrl(request.getImage(), cloudinary.uploader());
        }
        Event event = Event.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .eventDate(parseIsoDateTime(request.getEventDate()))
                .endDate(parseIsoDateTime(request.getEndDate()))
                .location(request.getLocation())
                .organizer(request.getOrganizer())
                .eventType(request.getEventType())
                .eventMode(EventMode.valueOf(request.getEventMode()))
                .targetAudience(request.getTargetAudience())
                .imageUrl(imageUrl)
                .registrationUrl(request.getMeetingLink())
                .maxParticipants(request.getMaxParticipants())
                .isActive(true)
                .build();

        Event savedEvent = eventRepository.save(event);
        
        // Send notifications to all non-admin users
        sendEventNotificationToUsers(savedEvent);
        
        return savedEvent;
    }
    
    private void sendEventNotificationToUsers(Event event) {
        // Get all users who are not ADMIN or SUPER_ADMIN
        userRepository.findAll().stream()
                .filter(user -> user.getRoles().stream()
                        .noneMatch(role -> role.name().equals("ADMIN") || role.name().equals("SUPER_ADMIN")))
                .forEach(user -> {
                    dev.gagnon.bfpcapi.dto.request.NotificationEvent notificationEvent = 
                        new dev.gagnon.bfpcapi.dto.request.NotificationEvent();
                    notificationEvent.setEmail(user.getEmail());
                    notificationEvent.setTitle("New Event: " + event.getTitle());
                    notificationEvent.setMessage("A new event has been added: " + event.getTitle() + 
                            ". Date: " + event.getEventDate());
                    notificationEvent.setType("EVENT");
                    
                    try {
                        notificationService.sendNotification(notificationEvent);
                    } catch (Exception e) {
                        // Log error but don't fail event creation
                        System.err.println("Failed to send notification to " + user.getEmail());
                    }
                });
    }

    @Override
    public List<Event> getUpcomingEvents() {
        return eventRepository.findUpcomingEvents(LocalDateTime.now());
    }

    @Override
    public List<Event> getAllActiveEvents() {
        return eventRepository.findByIsActiveTrue(true);
    }

    @Override
    public Event getEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new BusinessException("Event not found"));
    }

    @Override
    public Event updateEvent(Long eventId, EventRequest request) {
        Event event = getEventById(eventId);
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            String imageUrl = getMediaUrl(request.getImage(), cloudinary.uploader());
            event.setImageUrl(imageUrl);
        }
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setEventDate(parseIsoDateTime(request.getEventDate()));
        event.setEndDate(parseIsoDateTime(request.getEndDate()));
        event.setLocation(request.getLocation());
        event.setEventMode(EventMode.valueOf(request.getEventMode()));
        event.setOrganizer(request.getOrganizer());
        event.setEventType(request.getEventType());
        event.setTargetAudience(request.getTargetAudience());
        event.setRegistrationUrl(request.getMeetingLink());
        event.setMaxParticipants(request.getMaxParticipants());

        return eventRepository.save(event);
    }

    @Override
    public void deleteEvent(Long eventId) {
        Event event = getEventById(eventId);
        eventRepository.delete(event);
    }

    @Override
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @Override
    public List<Event> getAllByEventType(String eventType) {
        return eventRepository.findByEventType(eventType);
    }

    @Override
    public List<Event> getAllByEventMode(String mode) {
        return eventRepository.findByEventMode(EventMode.valueOf(mode.toUpperCase()));
    }

    @Override
    public Long getEventCount() {
        return (long) eventRepository
                .findAll()
                .size();
    }
}
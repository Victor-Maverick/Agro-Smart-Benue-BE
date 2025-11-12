package dev.gagnon.bfpcapi.service.impl;

import com.cloudinary.Cloudinary;
import dev.gagnon.bfpcapi.data.constants.EventMode;
import dev.gagnon.bfpcapi.data.model.Event;
import dev.gagnon.bfpcapi.data.repository.EventRepository;
import dev.gagnon.bfpcapi.dto.request.EventRequest;
import dev.gagnon.bfpcapi.exception.BusinessException;
import dev.gagnon.bfpcapi.service.EventService;
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

        return eventRepository.save(event);
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
        event.setActive(false);
        eventRepository.save(event);
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
}
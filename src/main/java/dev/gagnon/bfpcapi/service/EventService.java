package dev.gagnon.bfpcapi.service;

import dev.gagnon.bfpcapi.data.model.Event;
import dev.gagnon.bfpcapi.dto.request.EventRequest;

import java.util.List;

public interface EventService {
    Event createEvent(EventRequest request);
    List<Event> getUpcomingEvents();
    List<Event> getAllActiveEvents();
    Event getEventById(Long eventId);
    Event updateEvent(Long eventId, EventRequest request);
    void deleteEvent(Long eventId);

    List<Event> getAllEvents();

    List<Event> getAllByEventType(String eventType);

    List<Event> getAllByEventMode(String mode);
}
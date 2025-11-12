package dev.gagnon.bfpcapi.controller;

import dev.gagnon.bfpcapi.data.model.Event;
import dev.gagnon.bfpcapi.dto.request.EventRequest;
import dev.gagnon.bfpcapi.dto.response.BfpcApiResponse;
import dev.gagnon.bfpcapi.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {
    
    private final EventService eventService;

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<BfpcApiResponse<Event>> createEvent(@ModelAttribute EventRequest request) {
        Event event = eventService.createEvent(request);
        return ResponseEntity.ok(new BfpcApiResponse<>(true, event));
    }

    @GetMapping("/all-active")
    public ResponseEntity<BfpcApiResponse<?>>getAllActiveEvents(){
        List<Event> events = eventService.getAllActiveEvents();
        return ResponseEntity.ok(new BfpcApiResponse<>(true, events));
    }

    @GetMapping("/all-by-type")
    public ResponseEntity<BfpcApiResponse<?>>getAllByEventType(@RequestParam("eventType") String eventType){
        List<Event> events = eventService.getAllByEventType(eventType);
        return ResponseEntity.ok(new BfpcApiResponse<>(true, events));
    }

    @GetMapping("/all-by-mode")
    public ResponseEntity<BfpcApiResponse<?>>getAllByEventMode(@RequestParam("mode") String mode){
        List<Event> events = eventService.getAllByEventMode(mode);
        return ResponseEntity.ok(new BfpcApiResponse<>(true, events));
    }

    @GetMapping("/all")
    public ResponseEntity<BfpcApiResponse<?>> getAllEvents() {
        List<Event> events = eventService.getAllEvents();
        return ResponseEntity.ok(new BfpcApiResponse<>(true, events));
    }

    @GetMapping("/upcoming")
    public ResponseEntity<BfpcApiResponse<List<Event>>> getUpcomingEvents() {
        List<Event> events = eventService.getUpcomingEvents();
        return ResponseEntity.ok(new BfpcApiResponse<>(true, events));
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<BfpcApiResponse<Event>> getEvent(@PathVariable Long eventId) {
        Event event = eventService.getEventById(eventId);
        return ResponseEntity.ok(new BfpcApiResponse<>(true, event));
    }

    @PutMapping(value = "/{eventId}", consumes = {"multipart/form-data"})
    public ResponseEntity<BfpcApiResponse<Event>> updateEvent(@PathVariable Long eventId, @ModelAttribute EventRequest request) {
        Event event = eventService.updateEvent(eventId, request);
        return ResponseEntity.ok(new BfpcApiResponse<>(true, event));
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<BfpcApiResponse<Void>> deleteEvent(@PathVariable Long eventId) {
        eventService.deleteEvent(eventId);
        return ResponseEntity.ok(new BfpcApiResponse<>(true, null));
    }
}
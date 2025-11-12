package dev.gagnon.bfpcapi.data.repository;

import dev.gagnon.bfpcapi.data.constants.EventMode;
import dev.gagnon.bfpcapi.data.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    @Query("select e from Event e where e.isActive=:isActive")
    List<Event> findByIsActiveTrue(boolean isActive);
    List<Event> findByEventType(String eventType);
    @Query("SELECT e FROM Event e WHERE e.eventDate >= :startDate AND e.isActive = true ORDER BY e.eventDate ASC")
    List<Event> findUpcomingEvents(@Param("startDate") LocalDateTime startDate);
    @Query("SELECT e FROM Event e WHERE e.eventDate >= :startDate AND e.eventDate <= :endDate AND e.isActive = true ORDER BY e.eventDate ASC")
    List<Event> findEventsBetweenDates(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    @Query("select e from Event e where e.eventMode=:mode")
    List<Event> findByEventMode(EventMode mode);
}
package com.proiectcolectiv.demo.repository;

import com.proiectcolectiv.demo.model.EventOrganizer;
import com.proiectcolectiv.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventOrganizerRepository extends JpaRepository<EventOrganizer, UUID> {
    List<EventOrganizer> findAllByUserId(UUID userId);
    Optional<EventOrganizer> findByEventId(UUID eventId);
    List<EventOrganizer> findByEventIdIn(List<UUID> eventIds);
    
    @Query("SELECT eo.user FROM EventOrganizer eo WHERE eo.event.id = :eventId")
    List<User> findAllOrganizersForEvent(@Param("eventId") UUID eventId);
}

package com.proiectcolectiv.demo.repository;

import com.proiectcolectiv.demo.model.EventOrganizer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventOrganizerRepository extends JpaRepository<EventOrganizer, UUID> {
    List<EventOrganizer> findAllByUserId(UUID userId);
    Optional<EventOrganizer> findByEventId(UUID eventId);
    List<EventOrganizer> findByEventIdIn(List<UUID> eventIds);
}

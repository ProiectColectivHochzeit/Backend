package com.proiectcolectiv.demo.repository;

import com.proiectcolectiv.demo.model.EventOrganizer;
import com.proiectcolectiv.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface EventOrganizerRepository extends JpaRepository<EventOrganizer, UUID> {
    List<EventOrganizer> findAllByUserId(UUID eventId);

    @Query("SELECT u FROM User u " +
            "JOIN EventOrganizer as eo ON eo.user.id = u.id " +
            "JOIN Event as e ON eo.event.id = e.id " +
            "WHERE e.id = :eventId"
    )
    List<User> findAllOrganizersForEvent(UUID eventId);
}

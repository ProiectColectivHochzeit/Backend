package com.proiectcolectiv.demo.repository;

import com.proiectcolectiv.demo.model.EventParticipation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EventParticipationRepository extends JpaRepository<EventParticipation, UUID> {
    List<EventParticipation> findAllByUserId(UUID id);
}

package com.proiectcolectiv.demo.service;

import com.proiectcolectiv.demo.exception.eventParticipation.EventParticipationNotFound;
import com.proiectcolectiv.demo.model.EventParticipation;

import java.util.List;
import java.util.UUID;

public interface EventParticipationService {
    /**
     * Retrieves all event participations from the repository for a specific user.
     * @param userId the UUID of the user
     * @throws EventParticipationNotFound if no event participations are found for the user
     * @return List of EventParticipation objects associated with the specified user
     */
    List<EventParticipation> getAllEventParticipationByUserId(UUID userId) throws EventParticipationNotFound;
}

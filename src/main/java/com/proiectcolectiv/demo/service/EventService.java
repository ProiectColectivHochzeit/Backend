package com.proiectcolectiv.demo.service;


import com.proiectcolectiv.demo.exception.event.EventNotFoundException;
import com.proiectcolectiv.demo.exception.eventOrganizer.EventOrganizerNotFoundException;
import com.proiectcolectiv.demo.exception.eventParticipation.EventParticipationNotFound;
import com.proiectcolectiv.demo.model.Event;

import java.util.List;
import java.util.UUID;

public interface EventService {
    /**
     * Retrieves all events from the repository for a specific user.
     * @param userId the UUID of the user
     * @throws EventNotFoundException if no events are found for the specified user
     * @return List of Event objects associated with the specified user
     */
    List<Event> getAllEventsByUserId(UUID userId) throws EventNotFoundException, EventOrganizerNotFoundException, EventParticipationNotFound;
}

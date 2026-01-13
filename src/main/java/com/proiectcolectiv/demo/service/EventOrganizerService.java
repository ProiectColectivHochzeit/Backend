package com.proiectcolectiv.demo.service;

import com.proiectcolectiv.demo.exception.user.UserNotFoundException;
import com.proiectcolectiv.demo.model.EventOrganizer;
import com.proiectcolectiv.demo.model.User;

import java.util.List;
import java.util.UUID;

public interface EventOrganizerService {
    List<EventOrganizer> getAllEventOrganizerByUserId(UUID userId);

    List<User> getAllOrganizersForEvent(UUID eventId) throws UserNotFoundException;

    EventOrganizer createEventOrganizer(EventOrganizer eventOrganizer);

    /**
     * Check if a user is an organizer of a specific event
     * @param userId the unique identifier of the user
     * @return true if the user is an organizer of the event, false otherwise
     */
    boolean isUserOrganizerOfEvent(UUID userId, UUID eventId);
}

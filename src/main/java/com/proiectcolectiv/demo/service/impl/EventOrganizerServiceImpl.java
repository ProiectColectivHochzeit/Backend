package com.proiectcolectiv.demo.service.impl;

import com.proiectcolectiv.demo.model.EventOrganizer;
import com.proiectcolectiv.demo.model.User;
import com.proiectcolectiv.demo.repository.EventOrganizerRepository;
import com.proiectcolectiv.demo.service.EventOrganizerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventOrganizerServiceImpl implements EventOrganizerService {

    private final EventOrganizerRepository eventOrganizerRepository;

    @Override
    public List<EventOrganizer> getAllEventOrganizerByUserId(UUID userId) {
        return eventOrganizerRepository.findAllByUserId(userId);
    }

    @Override
    public List<User> getAllOrganizersForEvent(UUID eventId) {
        return eventOrganizerRepository.findAllOrganizersForEvent(eventId);
    }

    @Override
    public boolean isUserOrganizerOfEvent(UUID userId, UUID eventId){
        List<User> organizers = getAllOrganizersForEvent(eventId);
        for (User organizer : organizers) {
            if (organizer.getId().equals(userId)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public EventOrganizer createEventOrganizer(EventOrganizer eventOrganizer) {
        return eventOrganizerRepository.save(eventOrganizer);
    }
}

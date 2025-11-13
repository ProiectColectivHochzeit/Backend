package com.proiectcolectiv.demo.service.impl;

import com.proiectcolectiv.demo.exception.event.EventNotFoundException;
import com.proiectcolectiv.demo.exception.eventOrganizer.EventOrganizerNotFoundException;
import com.proiectcolectiv.demo.exception.eventParticipation.EventParticipationNotFound;
import com.proiectcolectiv.demo.model.Event;
import com.proiectcolectiv.demo.repository.EventRepository;
import com.proiectcolectiv.demo.service.EventOrganizerService;
import com.proiectcolectiv.demo.service.EventParticipationService;
import com.proiectcolectiv.demo.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventOrganizerService eventOrganizerService;
    private final EventParticipationService eventParticipationService;

    public List<Event> getAllEventsByUserId(UUID userId) throws EventNotFoundException, EventOrganizerNotFoundException, EventParticipationNotFound {
        List<Event> allUserRelatedEvents = Stream.concat(
                getAllEventsOrganizedByUserId(userId).stream(),
                getAllEventsParticipatedByUserId(userId).stream()
        ).toList();

        return allUserRelatedEvents;
    }

    public List<Event> getAllEventsOrganizedByUserId(UUID userId) throws EventNotFoundException, EventOrganizerNotFoundException {
        List<UUID> organizedEventIds = new java.util.ArrayList<>(eventOrganizerService.getAllEventOrganizerByUserId(userId)
                .stream()
                .map(eventOrganizer -> eventOrganizer.getEvent().getId())
                .toList());
        if (organizedEventIds.isEmpty()) {
            throw new EventNotFoundException();
        }

        return eventRepository.findAllById(organizedEventIds);
    }

    public List<Event> getAllEventsParticipatedByUserId(UUID userId) throws EventParticipationNotFound {
        List<UUID> participatedEventIds = eventParticipationService.getAllEventParticipationByUserId(userId)
                .stream()
                .map(eventParticipation -> eventParticipation.getEvent().getId())
                .toList();
        if (participatedEventIds.isEmpty()) {
            throw new EventParticipationNotFound();
        }

        return eventRepository.findAllById(participatedEventIds);
    }

}

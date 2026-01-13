package com.proiectcolectiv.demo.service.impl;

import com.proiectcolectiv.demo.dto.Event.EventRequestDTO;
import com.proiectcolectiv.demo.dto.Event.EventResponseDTO;
import com.proiectcolectiv.demo.exception.event.EventNotFoundException;
import com.proiectcolectiv.demo.exception.eventOrganizer.EventOrganizerNotFoundException;
import com.proiectcolectiv.demo.exception.eventParticipation.EventParticipationNotFound;
import com.proiectcolectiv.demo.model.Event;
import com.proiectcolectiv.demo.model.EventOrganizer;
import com.proiectcolectiv.demo.model.EventParticipation;
import com.proiectcolectiv.demo.model.User;
import com.proiectcolectiv.demo.repository.EventOrganizerRepository;
import com.proiectcolectiv.demo.repository.EventRepository;
import com.proiectcolectiv.demo.repository.UserRepository;
import com.proiectcolectiv.demo.service.EventOrganizerService;
import com.proiectcolectiv.demo.service.EventParticipationService;
import com.proiectcolectiv.demo.service.EventService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventOrganizerService eventOrganizerService;
    private final EventOrganizerRepository eventOrganizerRepository;
    private final EventParticipationService eventParticipationService;
    private final UserRepository userRepository;

    public List<Event> getAllEventsByUserId(UUID userId) throws EventNotFoundException, EventOrganizerNotFoundException, EventParticipationNotFound {
        List<Event> organizedEvents = getAllEventsOrganizedByUserId(userId);
        List<Event> participatedEvents = getAllEventsParticipatedByUserId(userId);
        
        // Combine and remove duplicates based on event ID
        Map<UUID, Event> eventMap = new java.util.HashMap<>();
        organizedEvents.forEach(e -> eventMap.put(e.getId(), e));
        participatedEvents.forEach(e -> eventMap.put(e.getId(), e));
        
        List<Event> allEvents = new java.util.ArrayList<>(eventMap.values());
        
        return allEvents;
    }

    public List<Event> getAllEventsOrganizedByUserId(UUID userId) throws EventOrganizerNotFoundException {
        List<UUID> organizedEventIds = new java.util.ArrayList<>(eventOrganizerService.getAllEventOrganizerByUserId(userId)
                .stream()
                .map(eventOrganizer -> eventOrganizer.getEvent().getId())
                .toList());

        return eventRepository.findAllById(organizedEventIds);
    }

    public List<Event> getAllEventsParticipatedByUserId(UUID userId) throws EventParticipationNotFound {
        try {
            List<UUID> participatedEventIds = eventParticipationService.getAllEventParticipationByUserId(userId)
                    .stream()
                    .map(eventParticipation -> eventParticipation.getEvent().getId())
                    .toList();

            if (participatedEventIds.isEmpty()) {
                return List.of(); // Return empty list if no participations
            }

            return eventRepository.findAllById(participatedEventIds);
        } catch (EventParticipationNotFound e) {
            // If no participations found, return empty list instead of throwing
            return List.of();
        }
    }


    @Override
    @Transactional
    public EventResponseDTO createEvent(EventRequestDTO dto) {
        User user = userRepository.findById(UUID.fromString(dto.getOrganizerId()))
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + dto.getOrganizerId()));

        Event event = new Event();
        event.setName(dto.getName());
        event.setStartingDate(dto.getStartingDate());
        event.setEndDate(dto.getEndDate());
        event.setLocation(dto.getLocation());

        // save event first so it has an id
        Event saved = eventRepository.save(event);

        // create and persist organizer entry using the service
        EventOrganizer organizer = new EventOrganizer();
        organizer.setEvent(saved);
        organizer.setUser(user);
        eventOrganizerService.createEventOrganizer(organizer);

        // create and persist participation entry so the user is an attendee
        EventParticipation participation = new EventParticipation();
        participation.setEvent(saved);
        participation.setUser(user);
        // optional: set participation status if required, e.g. participation.setStatus(Status.ACCEPTED);
        eventParticipationService.createEventParticipation(participation);

        return new EventResponseDTO(saved.getId(), saved.getName(),
                saved.getStartingDate(), saved.getEndDate(), saved.getLocation(), user.getId().toString());
    }

    @Override
    public EventResponseDTO getEventById(UUID eventId) throws EventNotFoundException {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException());

        // Get organizer ID
        String organizerId = eventOrganizerRepository.findByEventId(eventId)
                .map(organizer -> organizer.getUser().getId().toString())
                .orElse("");

        return new EventResponseDTO(
                event.getId(),
                event.getName(),
                event.getStartingDate(),
                event.getEndDate(),
                event.getLocation(),
                organizerId
        );
    }

    @Override
    public List<EventResponseDTO> getAllEventsByUserIdWithOrganizer(UUID userId) throws EventNotFoundException, EventOrganizerNotFoundException, EventParticipationNotFound {
        List<Event> events = getAllEventsByUserId(userId);

        if (events.isEmpty()) {
            return List.of();
        }

        // Get all event IDs
        List<UUID> eventIds = events.stream().map(Event::getId).toList();

        // Fetch all organizers for these events in one query
        List<EventOrganizer> organizers = eventOrganizerRepository.findByEventIdIn(eventIds);

        // Create a map of eventId -> organizerUserId
        Map<UUID, String> organizerMap = organizers.stream()
                .collect(Collectors.toMap(
                        org -> org.getEvent().getId(),
                        org -> org.getUser().getId().toString(),
                        (existing, replacement) -> existing // If duplicate, keep first
                ));

        // Convert events to DTOs with organizer IDs
        return events.stream()
                .map(event -> new EventResponseDTO(
                        event.getId(),
                        event.getName(),
                        event.getStartingDate(),
                        event.getEndDate(),
                        event.getLocation(),
                        organizerMap.getOrDefault(event.getId(), "")
                ))
                .toList();
    }

}

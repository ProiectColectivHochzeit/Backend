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
import com.proiectcolectiv.demo.repository.EventRepository;
import com.proiectcolectiv.demo.repository.UserRepository;
import com.proiectcolectiv.demo.service.EventOrganizerService;
import com.proiectcolectiv.demo.service.EventParticipationService;
import com.proiectcolectiv.demo.service.EventService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventOrganizerService eventOrganizerService;
    private final EventParticipationService eventParticipationService;
    private final UserRepository userRepository;

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

    @Override
    @Transactional
    public EventResponseDTO createEvent(EventRequestDTO dto, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));

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
                saved.getStartingDate(), saved.getEndDate(), saved.getLocation());
    }



}

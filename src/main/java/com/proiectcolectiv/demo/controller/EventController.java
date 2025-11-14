package com.proiectcolectiv.demo.controller;

import com.proiectcolectiv.demo.dto.Event.EventResponseDTO;
import com.proiectcolectiv.demo.exception.event.EventNotFoundException;
import com.proiectcolectiv.demo.exception.eventOrganizer.EventOrganizerNotFoundException;
import com.proiectcolectiv.demo.exception.eventParticipation.EventParticipationNotFound;
import com.proiectcolectiv.demo.mapper.EventMapper;
import com.proiectcolectiv.demo.model.Event;
import com.proiectcolectiv.demo.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;
    private final EventMapper eventMapper;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<EventResponseDTO>> getAllEventsByUserId(@PathVariable UUID userId) throws EventNotFoundException, EventParticipationNotFound, EventOrganizerNotFoundException {
        List<Event> events = eventService.getAllEventsByUserId(userId);
        List<EventResponseDTO> eventResponseDTOs = events.stream()
                .map(eventMapper::eventToEventResponseDTO)
                .toList();

        return ResponseEntity.ok(eventResponseDTOs);
    }
}

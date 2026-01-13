package com.proiectcolectiv.demo.controller;

import com.proiectcolectiv.demo.dto.Event.EventRequestDTO;
import com.proiectcolectiv.demo.dto.Event.EventResponseDTO;
import com.proiectcolectiv.demo.dto.photo.PhotoResponseDTO;
import com.proiectcolectiv.demo.exception.event.EventNotFoundException;
import com.proiectcolectiv.demo.exception.eventOrganizer.EventOrganizerNotFoundException;
import com.proiectcolectiv.demo.exception.eventParticipation.EventParticipationNotFound;
import com.proiectcolectiv.demo.mapper.EventMapper;
import com.proiectcolectiv.demo.model.Event;
import com.proiectcolectiv.demo.service.EventService;
import com.proiectcolectiv.demo.service.PhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    private final PhotoService photoService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<EventResponseDTO>> getAllEventsByUserId(@PathVariable UUID userId) throws EventNotFoundException, EventParticipationNotFound, EventOrganizerNotFoundException {
        List<EventResponseDTO> eventResponseDTOs = eventService.getAllEventsByUserIdWithOrganizer(userId);
        return ResponseEntity.ok(eventResponseDTOs);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponseDTO> getEventById(@PathVariable String eventId) {
        try {
            UUID eventUUID = UUID.fromString(eventId);
            EventResponseDTO event = eventService.getEventById(eventUUID);
            return ResponseEntity.ok(event);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (EventNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping
    public ResponseEntity<EventResponseDTO> createEvent(@RequestBody EventRequestDTO request) {
        String organizerID = request.getOrganizerID();
        if (organizerID == null || organizerID.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        EventResponseDTO response = eventService.createEvent(request, organizerID);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{eventId}/photos")
    public ResponseEntity<List<PhotoResponseDTO>> getPhotosByEventId(@PathVariable String eventId) {
        try {
            UUID eventUUID = UUID.fromString(eventId);
            List<PhotoResponseDTO> photos = photoService.getPhotosByEventId(eventUUID);
            return ResponseEntity.ok(photos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }
}

package com.proiectcolectiv.demo.controller;

import com.proiectcolectiv.demo.dto.Event.EventRequestDTO;
import com.proiectcolectiv.demo.dto.Event.EventResponseDTO;
import com.proiectcolectiv.demo.dto.photo.PhotoResponseDTO;
import com.proiectcolectiv.demo.dto.user.InvitedUserResponseDTO;
import com.proiectcolectiv.demo.exception.event.EventNotFoundException;
import com.proiectcolectiv.demo.exception.eventOrganizer.EventOrganizerNotFoundException;
import com.proiectcolectiv.demo.exception.eventParticipation.EventParticipationNotFound;
import com.proiectcolectiv.demo.mapper.EventMapper;
import com.proiectcolectiv.demo.model.Event;
import com.proiectcolectiv.demo.service.EventOrganizerService;
import com.proiectcolectiv.demo.service.EventService;
import com.proiectcolectiv.demo.service.PhotoService;
import com.proiectcolectiv.demo.service.InvitationService;
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
    private final InvitationService invitationService;
    private final EventOrganizerService eventOrganizerService;

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

    @GetMapping("/participants/{id}")
    public ResponseEntity<List<InvitedUserResponseDTO>> getAllParticipantsByEventId(@PathVariable UUID id) throws EventNotFoundException {
        List<InvitedUserResponseDTO> invitedParticipants = invitationService.getAllInvitedUsersByEvent(id);
        return ResponseEntity.ok(invitedParticipants);
    }

    @PostMapping
    public ResponseEntity<EventResponseDTO> createEvent(@RequestBody EventRequestDTO request) {
        String organizerID = request.getOrganizerId();
        if (organizerID == null || organizerID.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        EventResponseDTO response = eventService.createEvent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping("/isOrganizer/{userId}/{eventId}")
    public ResponseEntity<Boolean> isUserOrganizerOfEvent(@PathVariable UUID userId, @PathVariable UUID eventId) {
        boolean isOrganizer = eventOrganizerService.isUserOrganizerOfEvent(userId, eventId);
        return ResponseEntity.ok(isOrganizer);
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

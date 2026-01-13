package com.proiectcolectiv.demo.controller;

import com.proiectcolectiv.demo.dto.Event.EventRequestDTO;
import com.proiectcolectiv.demo.dto.Event.EventResponseDTO;
import com.proiectcolectiv.demo.dto.invitation.InvitationRequestDTO;
import com.proiectcolectiv.demo.dto.invitation.InvitationResponseDTO;
import com.proiectcolectiv.demo.dto.photo.PhotoResponseDTO;
import com.proiectcolectiv.demo.dto.user.InvitedUserResponseDTO;
import com.proiectcolectiv.demo.exception.event.EventNotFoundException;
import com.proiectcolectiv.demo.exception.eventOrganizer.EventOrganizerNotFoundException;
import com.proiectcolectiv.demo.exception.eventParticipation.EventParticipationNotFound;
import com.proiectcolectiv.demo.exception.user.UserNotFoundException;
import com.proiectcolectiv.demo.mapper.EventMapper;
import com.proiectcolectiv.demo.mapper.InvitationMapper;
import com.proiectcolectiv.demo.model.Invitation;
import com.proiectcolectiv.demo.service.EventOrganizerService;
import com.proiectcolectiv.demo.service.EventService;
import com.proiectcolectiv.demo.service.PhotoService;
import com.proiectcolectiv.demo.service.InvitationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private final InvitationMapper invitationMapper;
    private final com.proiectcolectiv.demo.service.ExcelImportService excelImportService;

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
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/{eventId}/participants")
    public ResponseEntity<List<InvitedUserResponseDTO>> getAllParticipantsByEventId(@PathVariable String eventId) {
        try {
            UUID eventUUID = UUID.fromString(eventId);
            List<InvitedUserResponseDTO> invitedParticipants = invitationService.getAllInvitedUsersByEvent(eventUUID);
            return ResponseEntity.ok(invitedParticipants);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
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

    @PostMapping("/{eventId}/invite")
    public ResponseEntity<?> inviteParticipant(
            @PathVariable String eventId,
            @RequestBody Map<String, String> request,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        try {
            UUID userId = extractUserIdFromToken(authorization);
            if (userId == null) {
                return ResponseEntity.status(401).body(Map.of("error", "Unauthorized: Invalid or missing token"));
            }

            String guestEmail = request.get("email");
            if (guestEmail == null || guestEmail.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email is required"));
            }

            InvitationRequestDTO invitationRequest = InvitationRequestDTO.builder()
                    .eventId(eventId)
                    .currentUserId(userId.toString())
                    .guestEmail(guestEmail)
                    .build();

            Invitation invitation = invitationMapper.invitationRequestDTOToInvitation(invitationRequest);
            Invitation createdInvitation = invitationService.createInvitation(invitation);
            InvitationResponseDTO responseDTO = invitationMapper.invitationToInvitationResponseDTO(createdInvitation);

            return ResponseEntity.ok(responseDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (EventNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Event not found"));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
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
            return ResponseEntity.status(500).build();
        }
    }

    @DeleteMapping("/{eventId}/invitations/{invitationId}")
    public ResponseEntity<?> deleteInvitation(
            @PathVariable String eventId,
            @PathVariable String invitationId,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        try {
            UUID userId = extractUserIdFromToken(authorization);
            if (userId == null) {
                return ResponseEntity.status(401).body(Map.of("error", "Unauthorized: Invalid or missing token"));
            }

            UUID invitationUUID = UUID.fromString(invitationId);
            invitationService.deleteInvitation(invitationUUID);
            return ResponseEntity.ok(Map.of("message", "Invitation deleted successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{eventId}/import-participants")
    public ResponseEntity<?> importParticipantsFromExcel(
            @PathVariable String eventId,
            @RequestParam("file") MultipartFile file,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        try {
            UUID userId = extractUserIdFromToken(authorization);
            if (userId == null) {
                return ResponseEntity.status(401).body(Map.of("error", "Unauthorized: Invalid or missing token"));
            }

            UUID eventUUID = UUID.fromString(eventId);
            // Verify event exists (getEventById throws EventNotFoundException if not found)
            try {
                eventService.getEventById(eventUUID);
            } catch (EventNotFoundException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Event not found"));
            }

            // Parse emails from Excel or CSV
            List<String> emails = excelImportService.parseEmailsFromFile(file);
            
            // Create invitations for each email
            Map<String, Object> result = new HashMap<>();
            int successCount = 0;
            int failureCount = 0;
            List<String> failedEmails = new ArrayList<>();

            for (String email : emails) {
                try {
                    InvitationRequestDTO invitationRequest = InvitationRequestDTO.builder()
                            .eventId(eventId)
                            .currentUserId(userId.toString())
                            .guestEmail(email)
                            .build();

                    Invitation invitation = invitationMapper.invitationRequestDTOToInvitation(invitationRequest);
                    invitationService.createInvitation(invitation);
                    successCount++;
                } catch (Exception e) {
                    failureCount++;
                    failedEmails.add(email);
                    // Log but continue with other emails
                }
            }

            result.put("message", "Import completed");
            result.put("total", emails.size());
            result.put("success", successCount);
            result.put("failed", failureCount);
            if (!failedEmails.isEmpty()) {
                result.put("failedEmails", failedEmails);
            }

            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (EventNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Event not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    private UUID extractUserIdFromToken(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return null;
        }

        try {
            String token = authorization.substring(7); // Remove "Bearer " prefix
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return null;
            }

            // Decode the payload (second part)
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
            // Parse JSON to get "sub" claim
            int subIndex = payload.indexOf("\"sub\":\"");
            if (subIndex == -1) {
                return null;
            }
            int startIndex = subIndex + 7;
            int endIndex = payload.indexOf("\"", startIndex);
            if (endIndex == -1) {
                return null;
            }
            String userIdString = payload.substring(startIndex, endIndex);
            return UUID.fromString(userIdString);
        } catch (Exception e) {
            return null;
        }
    }
}

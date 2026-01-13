package com.proiectcolectiv.demo.mapper;

import com.proiectcolectiv.demo.dto.Event.EventResponseDTO;
import com.proiectcolectiv.demo.dto.invitation.InvitationRequestDTO;
import com.proiectcolectiv.demo.dto.invitation.InvitationResponseDTO;
import com.proiectcolectiv.demo.exception.event.EventNotFoundException;
import com.proiectcolectiv.demo.exception.user.UserNotFoundException;
import com.proiectcolectiv.demo.model.Event;
import com.proiectcolectiv.demo.model.Invitation;
import com.proiectcolectiv.demo.model.User;
import com.proiectcolectiv.demo.model.enums.Status;
import com.proiectcolectiv.demo.repository.EventRepository;
import com.proiectcolectiv.demo.service.EventService;
import com.proiectcolectiv.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.UUID;

@RequiredArgsConstructor
@Service
public class InvitationMapper {
    private final UserService userService;
    private final EventService eventService;
    private final EventRepository eventRepository;

    public Invitation invitationRequestDTOToInvitation(InvitationRequestDTO dto) throws UserNotFoundException, EventNotFoundException {
        // Try to find the invited user by email first
        User invitedUser = null;
        try {
            invitedUser = userService.getUserByEmail(dto.getGuestEmail());
        } catch (UserNotFoundException e) {
            // User doesn't exist yet - this is OK, they'll be set when they accept
            // For now, we need a placeholder user since the entity requires it
            // We'll use the organizer as a temporary placeholder
            if (dto.getCurrentUserId() != null && !dto.getCurrentUserId().isBlank()) {
                try {
                    invitedUser = userService.getUserById(UUID.fromString(dto.getCurrentUserId()));
                } catch (IllegalArgumentException ex) {
                    // If organizer not found, we'll need to handle this
                }
            }
        }

        Event event;
        try {
            UUID eventId = UUID.fromString(dto.getEventId());
            event = eventRepository.findById(eventId)
                    .orElseThrow(() -> new EventNotFoundException());
        } catch (IllegalArgumentException e) {
            throw new EventNotFoundException();
        }

        return new Invitation(null, event, invitedUser, dto.getGuestEmail(), Status.PENDING);
    }

    public InvitationResponseDTO invitationToInvitationResponseDTO(Invitation invitation) {
        return InvitationResponseDTO.builder()
                .id(invitation.getId())
                .event(invitation.getEvent())
                .user(invitation.getUser())
                .guestEmail(invitation.getGuestEmail())
                .status(invitation.getStatus().name())
                .build();
    }
}



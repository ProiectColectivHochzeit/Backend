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
        User user = null;
        if (dto.getCurrentUserId() != null && !dto.getCurrentUserId().isBlank()) {
            try {
                user = userService.getUserById(UUID.fromString(dto.getCurrentUserId()));
            } catch (IllegalArgumentException e) {
                throw new UserNotFoundException();
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

        return new Invitation(null, event, user, dto.getGuestEmail(), Status.PENDING);
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



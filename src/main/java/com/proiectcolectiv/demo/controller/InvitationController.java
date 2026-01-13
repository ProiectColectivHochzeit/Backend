package com.proiectcolectiv.demo.controller;

import com.proiectcolectiv.demo.dto.invitation.AcceptInvitationDTO;
import com.proiectcolectiv.demo.dto.invitation.InvitationRequestDTO;
import com.proiectcolectiv.demo.dto.invitation.InvitationResponseDTO;
import com.proiectcolectiv.demo.exception.event.EventNotFoundException;
import com.proiectcolectiv.demo.exception.user.UserNotFoundException;
import com.proiectcolectiv.demo.mapper.InvitationMapper;
import com.proiectcolectiv.demo.model.Invitation;
import com.proiectcolectiv.demo.service.InvitationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/invitations")
public class InvitationController {
    private final InvitationService invitationService;
    private final InvitationMapper invitationMapper;


    @GetMapping("{id}")
    public ResponseEntity<InvitationResponseDTO> getInvitation(@PathVariable("id") UUID id) {
        Invitation invitation = invitationService.getInvitationById(id);
        InvitationResponseDTO responseDTO = invitationMapper.invitationToInvitationResponseDTO(invitation);
        return ResponseEntity.ok(responseDTO);

    }

    @PostMapping("decline/{id}")
    public ResponseEntity<?> declineInvitation(@PathVariable("id") UUID id) {
        try {
            invitationService.declineInvitation(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        }
    }

    @PostMapping()
    public ResponseEntity<InvitationResponseDTO> createInvitation(@Valid @RequestBody InvitationRequestDTO dto) throws UserNotFoundException, EventNotFoundException {

        Invitation invitation = invitationMapper.invitationRequestDTOToInvitation(dto);
        Invitation createdInvitation = invitationService.createInvitation(invitation);
        InvitationResponseDTO responseDTO = invitationMapper.invitationToInvitationResponseDTO(createdInvitation);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("validate/{id}")
    public ResponseEntity<?> validateInvitation(@PathVariable("id") UUID id) {
        boolean isValid = invitationService.isValid(id);
        if (isValid) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().body("Invitation is not valid");
        }
    }

    @PostMapping("accept")
    public ResponseEntity<?> acceptInvitation(@RequestBody AcceptInvitationDTO acceptInvitationDTO) {
        try {
            // Validate UUID format
            if (acceptInvitationDTO.getInvitationId() == null) {
                return ResponseEntity.status(400).body("Invitation ID is required");
            }
            if (acceptInvitationDTO.getInvitedUserId() == null) {
                return ResponseEntity.status(400).body("User ID is required");
            }
            
            invitationService.acceptInvitation(acceptInvitationDTO);
            return ResponseEntity.ok().build();
        } catch (EventNotFoundException e) {
            return ResponseEntity.status(404).body("Event not found");
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(404).body("User not found");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (RuntimeException e) {
            if (e.getMessage() != null && e.getMessage().contains("not found")) {
                return ResponseEntity.status(404).body(e.getMessage());
            }
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        }
    }
}

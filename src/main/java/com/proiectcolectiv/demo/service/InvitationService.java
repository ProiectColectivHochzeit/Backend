package com.proiectcolectiv.demo.service;

import com.proiectcolectiv.demo.dto.invitation.AcceptInvitationDTO;
import com.proiectcolectiv.demo.dto.user.InvitedUserResponseDTO;
import com.proiectcolectiv.demo.exception.event.EventNotFoundException;
import com.proiectcolectiv.demo.exception.user.UserNotFoundException;
import com.proiectcolectiv.demo.model.Invitation;

import java.util.List;
import java.util.UUID;

public interface InvitationService {

    /**
     * Creates a new invitation and sends an email to the invited user.
     * @param invitation the invitation to be created
     */
    Invitation createInvitation(Invitation invitation);

    /**
     * Retrieves an invitation by its unique identifier.
     * @param id the unique identifier of the invitation
     * @return the invitation with the specified id
     */
    Invitation getInvitationById(UUID id);

    /**
     * Retrieves all invitations associated with a specific event.
     * @param eventId the unique identifier of the event
     * @return a list of invitations for the specified event
     */
    List<Invitation> getAllInvitationsByEvent(UUID eventId);

    /**
     * Retrieves all invited users for a specific event.
     * @param eventId the unique identifier of the event
     * @return a list of invited user response DTOs for the specified event
     */
    List<InvitedUserResponseDTO> getAllInvitedUsersByEvent(UUID eventId);


    /**
     * Sets the status of the invitation to DECLINED.
     * @param id the unique identifier of the invitation to be declined
     */
    void declineInvitation(UUID id);


    /**
     * Sets the status of the invitation to ACCEPTED and creates an event participation for the user.
     * The function also checks if the invitation is 10 days apart from the event date and if so, it automatically declines the invitation.
     * @param acceptInvitationDTO the DTO containing information to accept the invitation
     * @throws EventNotFoundException
     * @throws UserNotFoundException
     */
    void acceptInvitation(AcceptInvitationDTO acceptInvitationDTO) throws EventNotFoundException, UserNotFoundException;


    // Validates if the invitation is still valid (not expired or already accepted/declined).
    boolean isValid(UUID id);
}

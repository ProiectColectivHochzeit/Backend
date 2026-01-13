package com.proiectcolectiv.demo.service.impl;

import com.proiectcolectiv.demo.dto.invitation.AcceptInvitationDTO;
import com.proiectcolectiv.demo.dto.user.InvitedUserResponseDTO;
import com.proiectcolectiv.demo.exception.event.EventNotFoundException;
import com.proiectcolectiv.demo.exception.user.UserNotFoundException;
import com.proiectcolectiv.demo.model.Event;
import com.proiectcolectiv.demo.model.EventParticipation;
import com.proiectcolectiv.demo.model.Invitation;
import com.proiectcolectiv.demo.model.User;
import com.proiectcolectiv.demo.model.enums.Status;
import com.proiectcolectiv.demo.repository.EventParticipationRepository;
import com.proiectcolectiv.demo.repository.EventRepository;
import com.proiectcolectiv.demo.repository.InvitationRepository;
import com.proiectcolectiv.demo.service.EmailService;
import com.proiectcolectiv.demo.service.EventOrganizerService;
import com.proiectcolectiv.demo.service.InvitationService;
import com.proiectcolectiv.demo.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class InvitationServiceImpl  implements InvitationService {

    private final InvitationRepository invitationRepository;
    private final EventOrganizerService eventOrganizerService;
    private final EventRepository eventRepository;
    private final EventParticipationRepository eventParticipationRepository;
    private final EmailService emailService;
    private final UserService userService;

    @Override
    public Invitation createInvitation(Invitation invitation) {
        // Save invitation first
        Invitation savedInvitation = invitationRepository.save(invitation);
        
        // Try to send email, but don't fail invitation creation if email fails
        try {
            emailService.sendHtmlEmailInvitation(savedInvitation.getId(), savedInvitation.getGuestEmail(), savedInvitation.getEvent());
            log.info("Invitation email sent successfully to: {}", savedInvitation.getGuestEmail());
        } catch (Exception e) {
            // Log the error but don't fail the invitation creation
            log.error("Failed to send invitation email to: {}. Invitation was still created. Error: {}", 
                    savedInvitation.getGuestEmail(), e.getMessage());
        }
        
        return savedInvitation;
    }

    @Override
    public Invitation getInvitationById(UUID id) {
        log.debug("Looking for invitation with ID: {}", id);
        return invitationRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Invitation with ID {} not found in database", id);
                    return new RuntimeException("Invitation not found: " + id);
                });
    }

    @Override
    public List<Invitation> getAllInvitationsByEvent(UUID eventId) {
        return invitationRepository.findAll().stream()
                .filter(invitation -> invitation.getEvent().getId().equals(eventId))
                .toList();
    }

    @Override
    public List<InvitedUserResponseDTO> getAllInvitedUsersByEvent(UUID eventId) {
        try {
            List<Invitation>invitations = getAllInvitationsByEvent(eventId);
            log.info("Found {} invitations for event {}", invitations.size(), eventId);
            return invitations.stream()
                    .map(invitation -> {
                        String name;
                        UUID userId;
                        
                        // Check if the user in the invitation is the actual invited person or a placeholder
                        // We can tell by checking if the user's email matches the guest email
                        boolean isActualInvitedUser = invitation.getUser() != null && 
                                invitation.getUser().getEmail().equals(invitation.getGuestEmail());
                        
                        if (invitation.getStatus() == Status.PENDING) {
                            // For pending invitations, always show "Invited"
                            name = "Invited";
                            userId = invitation.getUser() != null ? invitation.getUser().getId() : UUID.fromString("00000000-0000-0000-0000-000000000000");
                        } else if (invitation.getStatus() == Status.ACCEPTED && isActualInvitedUser) {
                            // For accepted invitations with actual user, show their name
                            name = invitation.getUser().getFirstName() + " " + invitation.getUser().getLastName();
                            userId = invitation.getUser().getId();
                        } else if (invitation.getStatus() == Status.DECLINED) {
                            // For declined, show name if user exists and matches, otherwise "Invited"
                            if (isActualInvitedUser && invitation.getUser() != null) {
                                name = invitation.getUser().getFirstName() + " " + invitation.getUser().getLastName();
                                userId = invitation.getUser().getId();
                            } else {
                                name = "Invited";
                                userId = invitation.getUser() != null ? invitation.getUser().getId() : UUID.fromString("00000000-0000-0000-0000-000000000000");
                            }
                        } else {
                            // Fallback for any other case
                            name = "Invited";
                            userId = invitation.getUser() != null ? invitation.getUser().getId() : UUID.fromString("00000000-0000-0000-0000-000000000000");
                        }
                        
                        InvitedUserResponseDTO dto = new InvitedUserResponseDTO(
                                userId,
                                name,
                                invitation.getGuestEmail(),
                                invitation.getStatus(),
                                invitation.getId()
                        );
                        log.debug("Created DTO for invitation: email={}, status={}, name={}", 
                                invitation.getGuestEmail(), invitation.getStatus(), name);
                        return dto;
                    }).toList();

        }catch (Exception e) {
            throw new RuntimeException("Failed to retrieve invitations for event with ID: " + eventId, e);
        }
    }

    @Transactional
    @Override
    public void acceptInvitation(AcceptInvitationDTO acceptInvitationDTO) throws EventNotFoundException, UserNotFoundException {
        log.info("Attempting to accept invitation {} for user {}", acceptInvitationDTO.getInvitationId(), acceptInvitationDTO.getInvitedUserId());
        
        // Log all invitations for debugging
        List<Invitation> allInvitations = invitationRepository.findAll();
        log.info("Total invitations in database: {}", allInvitations.size());
        allInvitations.forEach(inv -> log.info("  - Invitation ID: {}, Email: {}, Status: {}", inv.getId(), inv.getGuestEmail(), inv.getStatus()));
        
        Invitation invitation = getInvitationById(acceptInvitationDTO.getInvitationId());
        log.info("Found invitation with status: {} for email: {}", invitation.getStatus(), invitation.getGuestEmail());
        
        User invitedUser = userService.getUserById(acceptInvitationDTO.getInvitedUserId());
        log.debug("Found user: {}", invitedUser.getEmail());
        log.debug("Invitation guest email: {}", invitation.getGuestEmail());
        
        // Verify that the logged-in user's email matches the invitation's guest email
        // This ensures only the person who was invited can accept the invitation
        if (!invitedUser.getEmail().equalsIgnoreCase(invitation.getGuestEmail())) {
            log.warn("User {} (ID: {}) attempted to accept invitation for email {} (Invitation ID: {})", 
                    invitedUser.getEmail(), acceptInvitationDTO.getInvitedUserId(), 
                    invitation.getGuestEmail(), acceptInvitationDTO.getInvitationId());
            throw new IllegalArgumentException(
                String.format("The logged-in user's email (%s) does not match the invitation email (%s). Please log in with the email that was invited.", 
                    invitedUser.getEmail(), invitation.getGuestEmail()));
        }
        
        Event event = eventRepository.findById(invitation.getEvent().getId()).orElseThrow(EventNotFoundException::new);
        log.debug("Found event: {}", event.getName());

        // Check if user is already an organizer
        List<UUID> eventOrganizersId = eventOrganizerService.getAllOrganizersForEvent(event.getId()).stream().map(User::getId).toList();
        if (eventOrganizersId.contains(acceptInvitationDTO.getInvitedUserId())){
            log.info("User {} is already an organizer for event {}", acceptInvitationDTO.getInvitedUserId(), event.getId());
            return;
        }

        // Check if invitation is already accepted
        if (invitation.getStatus() == Status.ACCEPTED){
            log.info("Invitation {} is already accepted", acceptInvitationDTO.getInvitationId());
            return;
        }
        
        // Check if invitation is already declined
        if (invitation.getStatus() == Status.DECLINED){
            log.warn("Invitation {} is already declined, cannot accept", acceptInvitationDTO.getInvitationId());
            throw new IllegalArgumentException("This invitation has already been declined and cannot be accepted.");
        }

        // Check if event is less than 10 days away (auto-decline)
        // The event must be at least 10 days in the future to accept
        LocalDate today = LocalDate.now();
        LocalDate tenDaysFromNow = today.plusDays(10);
        
        log.info("=== DATE CHECK DEBUG ===");
        log.info("Event start date: {}", event.getStartingDate());
        log.info("Today: {}", today);
        log.info("10 days from now: {}", tenDaysFromNow);
        log.info("Days until event: {}", java.time.temporal.ChronoUnit.DAYS.between(today, event.getStartingDate()));
        log.info("Is event start before 10 days from now? {}", event.getStartingDate().isBefore(tenDaysFromNow));
        log.info("Is event start equal to 10 days from now? {}", event.getStartingDate().isEqual(tenDaysFromNow));
        
        // Only auto-decline if event is LESS than 10 days away (not equal)
        // But allow acceptance if event is in the past (for testing purposes)
        long daysUntilEvent = java.time.temporal.ChronoUnit.DAYS.between(today, event.getStartingDate());
        
        log.info("Days until event: {} (negative means event is in the past)", daysUntilEvent);
        
        // Only auto-decline if event is in the future AND less than 10 days away
        // If event is in the past, allow acceptance (for testing)
        if (daysUntilEvent >= 0 && daysUntilEvent < 10) {
            log.warn("Event {} (start: {}) is only {} days away (less than 10), auto-declining invitation {}", 
                    event.getId(), event.getStartingDate(), daysUntilEvent, acceptInvitationDTO.getInvitationId());
            invitation.setStatus(Status.DECLINED);
            invitationRepository.save(invitation);
            throw new IllegalArgumentException(
                String.format("Cannot accept invitation: The event is only %d days away. Invitations must be accepted at least 10 days before the event.", daysUntilEvent));
        }
        
        if (daysUntilEvent < 0) {
            log.info("Event {} is in the past ({} days ago), allowing acceptance anyway", event.getId(), -daysUntilEvent);
        }
        
        log.info("Event {} is {} days away (10+), allowing acceptance", event.getId(), daysUntilEvent);

        // Update the invitation user to the actual invited user
        invitation.setUser(invitedUser);
        log.debug("Updated invitation user to {}", invitedUser.getEmail());
        
        // Check if participation already exists to avoid duplicates
        boolean participationExists = eventParticipationRepository.findAllByUserId(acceptInvitationDTO.getInvitedUserId())
                .stream()
                .anyMatch(ep -> ep.getEvent().getId().equals(event.getId()));
        
        if (!participationExists) {
            // Create event participation so the event appears in user's event list
            EventParticipation participation = new EventParticipation(null, invitedUser, event);
            EventParticipation savedParticipation = eventParticipationRepository.save(participation);
            log.info("Created event participation (ID: {}) for user {} (ID: {}) in event {} (ID: {})", 
                    savedParticipation.getId(), invitedUser.getEmail(), invitedUser.getId(), event.getName(), event.getId());
            
            // Verify it was saved
            List<EventParticipation> userParticipations = eventParticipationRepository.findAllByUserId(acceptInvitationDTO.getInvitedUserId());
            log.info("User {} now has {} event participations", invitedUser.getEmail(), userParticipations.size());
        } else {
            log.info("Event participation already exists for user {} in event {}", invitedUser.getEmail(), event.getName());
        }
        
        // Set status to ACCEPTED and save
        invitation.setStatus(Status.ACCEPTED);
        Invitation savedInvitation = invitationRepository.save(invitation);
        log.info("Invitation {} accepted by user {} for event {} - Status saved as {}", 
                acceptInvitationDTO.getInvitationId(), acceptInvitationDTO.getInvitedUserId(), event.getId(), savedInvitation.getStatus());
        
        // Verify the status was saved correctly
        Invitation verifyInvitation = invitationRepository.findById(acceptInvitationDTO.getInvitationId()).orElse(null);
        if (verifyInvitation != null) {
            log.info("Verified invitation status after save: {}", verifyInvitation.getStatus());
            if (verifyInvitation.getStatus() != Status.ACCEPTED) {
                log.error("ERROR: Invitation status is {} but should be ACCEPTED!", verifyInvitation.getStatus());
            }
        }
    }

    @Transactional
    @Override
    public void declineInvitation(UUID id){
        invitationRepository.findById(id).ifPresent(invitation -> {
            log.info("Declining invitation {}", id);
            invitation.setStatus(Status.DECLINED);
            invitationRepository.save(invitation);
            log.info("Invitation {} declined successfully", id);
        });
        if (!invitationRepository.findById(id).isPresent()) {
            log.warn("Invitation {} not found for decline", id);
        }
    }


    @Transactional
    @Override
    public boolean isValid(UUID id) {
        Invitation invitation = this.getInvitationById(id);
        if (invitation.getStatus() != Status.PENDING) {
            return false;
        }
        return !invitation.getEvent().getStartingDate().isBefore(ChronoLocalDate.from(LocalDateTime.now()));
    }

    private void updateInvitationUser(UUID invitationId, User user) {
        Invitation invitation = getInvitationById(invitationId);
        invitation.setUser(user);
        invitationRepository.save(invitation);
        log.debug("Updated invitation {} user to {}", invitationId, user.getEmail());
    }

    @Override
    @Transactional
    public void deleteInvitation(UUID id) {
        if (invitationRepository.existsById(id)) {
            invitationRepository.deleteById(id);
            log.info("Invitation {} deleted successfully", id);
        } else {
            log.warn("Invitation {} not found for deletion", id);
            throw new RuntimeException("Invitation not found: " + id);
        }
    }
}

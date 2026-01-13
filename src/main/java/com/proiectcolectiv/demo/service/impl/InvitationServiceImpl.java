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
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class InvitationServiceImpl  implements InvitationService {

    private final InvitationRepository invitationRepository;
    private final EventOrganizerService eventOrganizerService;
    private final EventRepository eventRepository;
    private final EventParticipationRepository eventParticipationRepository;
    private final EmailService emailService;
    private final UserService userService;

    @Override
    public Invitation createInvitation(Invitation invitation) {
        try {
            Invitation savedInvitation = invitationRepository.save(invitation);
            emailService.sendHtmlEmailInvitation(invitation.getId(), invitation.getGuestEmail(), invitation.getEvent());
            return invitation;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create invitation", e);
        }
    }

    @Override
    public Invitation getInvitationById(UUID id) {
        return invitationRepository.findById(id).orElseThrow(() -> new RuntimeException("Invitation not found"));
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
            return invitations.stream()
                    .map(invitation -> {
                        String status = invitation.getStatus().toString();
                        String name = status.equals("PENDING") ? "N/A" : invitation.getUser().getFirstName() + " " + invitation.getUser().getLastName();
                        return new InvitedUserResponseDTO(
                                invitation.getUser().getId(),
                                name,
                                invitation.getGuestEmail(),
                                invitation.getStatus()
                        );
                    }).toList();

        }catch (Exception e) {
            throw new RuntimeException("Failed to retrieve invitations for event with ID: " + eventId, e);
        }
    }

    @Transactional
    @Override
    public void acceptInvitation(AcceptInvitationDTO acceptInvitationDTO) throws EventNotFoundException, UserNotFoundException {
        Invitation invitation = getInvitationById(acceptInvitationDTO.getInvitationId());
        User invitedUser = userService.getUserById(acceptInvitationDTO.getInvitedUserId());
        updateInvitationUser(acceptInvitationDTO.getInvitationId(), invitedUser);

        Event event = eventRepository.findById(invitation.getEvent().getId()).orElseThrow(EventNotFoundException::new);

        List<UUID> eventOrganizersId = eventOrganizerService.getAllOrganizersForEvent(event.getId()).stream().map(User::getId).toList();
        if (eventOrganizersId.contains(acceptInvitationDTO.getInvitedUserId())){
            return;
        }

        if (event.getStartingDate().isBefore(ChronoLocalDate.from(LocalDateTime.now().plusDays(10)))) {
            invitation.setStatus(Status.DECLINED);
            invitationRepository.save(invitation);
        }

        if (invitation.getStatus() == Status.ACCEPTED){
            return;
        }

        eventParticipationRepository.save(new EventParticipation(null, invitedUser, event));
        invitation.setStatus(Status.ACCEPTED);
        invitationRepository.save(invitation);
    }

    @Transactional
    @Override
    public void declineInvitation(UUID id){
        invitationRepository.findById(id).ifPresent(invitation -> {
            invitation.setStatus(Status.DECLINED);
            invitationRepository.save(invitation);
        });
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
    }
}

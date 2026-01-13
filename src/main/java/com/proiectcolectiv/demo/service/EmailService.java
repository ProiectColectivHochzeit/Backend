package com.proiectcolectiv.demo.service;

import com.proiectcolectiv.demo.model.Event;
import jakarta.mail.MessagingException;

import java.util.UUID;

public interface EmailService {

    /**
     * Sends an email to the specified recipient with the given subject and body.
     * @param recipient the email address of the recipient
     * @param subject the subject of the email
     * @param body the body content of the email
     * @throws MessagingException if there is an error while sending the email
     */
    void sendEmail(String recipient, String subject, String body) throws MessagingException;


    /**
     * Sends an HTML email invitation for an event to the specified recipient.
     * @param invitationId the unique identifier for the invitation
     * @param recipient the email address of the recipient
     * @param event the event details to be included in the invitation
     * @throws MessagingException if there is an error while sending the email
     */
    void sendHtmlEmailInvitation(UUID invitationId, String recipient, Event event) throws MessagingException;


}

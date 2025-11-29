package com.proiectcolectiv.demo.service;

import jakarta.mail.MessagingException;

public interface EmailService {

    /**
     * Sends an email to the specified recipient with the given subject and body.
     * @param recipient the email address of the recipient
     * @param subject the subject of the email
     * @param body the body content of the email
     * @throws MessagingException if there is an error while sending the email
     */
    void sendEmail(String recipient, String subject, String body) throws MessagingException;

    //Example method to send an HTML email
    void sendHtmlEmail(String recipient) throws MessagingException;


}

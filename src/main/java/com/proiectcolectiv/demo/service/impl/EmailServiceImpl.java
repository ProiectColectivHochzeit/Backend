package com.proiectcolectiv.demo.service.impl;

import com.proiectcolectiv.demo.model.Event;
import com.proiectcolectiv.demo.repository.EventOrganizerRepository;
import com.proiectcolectiv.demo.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.UUID;


@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final EventOrganizerRepository eventOrganizerRepository;
    private final SpringTemplateEngine templateEngine;
    private final JavaMailSender mailSender;

    private String mailSubject;

    @Value("classpath:static/images/morpheus.jpg")
    private Resource neoImage;
    @Value("classpath:static/images/blue_pill.jpg")
    private Resource bluePill;
    @Value("classpath:static/images/red_pill.jpg")
    private Resource redPill;


    public void sendEmail(String recipient, String subject, String body) throws MessagingException {


        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(recipient);
        helper.setSubject(subject);
        helper.setText(body, true);
        helper.addInline("morpheusImage", neoImage);
        helper.addInline("bluePill", bluePill);
        helper.addInline("redPill", redPill);

        mailSender.send(message);
    }

    @Override
    public void sendHtmlEmailInvitation(UUID invitationId, String recipient, Event event) throws MessagingException {
        Context thymeleafContext = new Context();

        thymeleafContext.setVariable("eventName", event.getName());
        thymeleafContext.setVariable("eventLocation", event.getLocation());
        thymeleafContext.setVariable("eventStartDate", event.getStartingDate());
        thymeleafContext.setVariable("eventEndDate", event.getEndDate());
        thymeleafContext.setVariable("eventOrganizers", eventOrganizerRepository.findAllOrganizersForEvent(event.getId()).stream().map(user -> user.getFirstName() + " " + user.getLastName()).toList());
        thymeleafContext.setVariable("invitationId", invitationId.toString());
        thymeleafContext.setVariable("morpheusCid", "morpheusImage");
        thymeleafContext.setVariable("bluePillCid", "bluePill");
        thymeleafContext.setVariable("redPillCid", "redPill");

        String htmlBody = templateEngine.process("templates/invitation.html", thymeleafContext);

        mailSubject = "You're Invited!";

        sendEmail(recipient, mailSubject, htmlBody);
    }


}

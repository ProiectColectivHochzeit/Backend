package com.proiectcolectiv.demo.service.impl;

import com.proiectcolectiv.demo.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;


@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final SpringTemplateEngine templateEngine;
    private final JavaMailSender mailSender;

    private String mailSubject;


    public void sendEmail(String recipient, String subject, String body) throws MessagingException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(recipient);
        helper.setSubject(subject);
        helper.setText(body, true);

        mailSender.send(message);
    }

    public void sendHtmlEmail(String recipient) throws MessagingException {
        Context thymeleafContext = new Context();

        thymeleafContext.setVariable("recipientName", recipient);
        String htmlBody = templateEngine.process("templates/example.html", thymeleafContext);

        mailSubject = "Welcome to Our Service";

        sendEmail(recipient, mailSubject, htmlBody);
    }


}

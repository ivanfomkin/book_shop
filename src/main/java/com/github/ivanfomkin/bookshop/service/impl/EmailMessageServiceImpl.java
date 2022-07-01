package com.github.ivanfomkin.bookshop.service.impl;

import com.github.ivanfomkin.bookshop.service.EmailMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

@Service
public class EmailMessageServiceImpl implements EmailMessageService {
    private static final String MESSAGE_TEXT = "Ваш код подтверждения: ";
    private static final String SUBJECT = "Подтвердите адрес электронной почты";
    private final MailSender mailSender;

    @Value("${spring.mail.username}")
    private String sendAddress;

    public EmailMessageServiceImpl(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendMessage(String emailAddress, String code) {
        var message = new SimpleMailMessage();
        message.setFrom(sendAddress);
        message.setTo(emailAddress);
        message.setText(MESSAGE_TEXT + code);
        message.setSubject(SUBJECT);
        mailSender.send(message);
    }
}

package com.github.ivanfomkin.bookshop.service.impl;

import com.github.ivanfomkin.bookshop.service.EmailMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Slf4j
@Service
public class EmailMessageServiceImpl implements EmailMessageService {
    private final MailSender mailSender;
    private final MessageSource messageSource;

    @Value("${spring.mail.username}")
    private String sendAddress;

    @Value("${bookshop.host}")
    private String applicationHost;

    public EmailMessageServiceImpl(MailSender mailSender, MessageSource messageSource) {
        this.mailSender = mailSender;
        this.messageSource = messageSource;
    }

    @Override
    public void sendConfirmMessage(String emailAddress, String code) {
        var message = createMailMessage(emailAddress);
        Locale locale = LocaleContextHolder.getLocale();
        var emailConfirmMessageText = messageSource.getMessage("profile.email.confirm.text", new Object[]{}, locale);
        var emailConfirmSubject = messageSource.getMessage("profile.email.confirm.subject", new Object[]{}, locale);
        message.setText(emailConfirmMessageText + "\n" + code);
        message.setSubject(emailConfirmSubject);
        mailSender.send(message);
    }

    @Override
    public void sendChangeDataMessage(String emailAddress, String token) {
        var message = createMailMessage(emailAddress);
        var locale = LocaleContextHolder.getLocale();
        var messageText = messageSource.getMessage("profile.email.edit.text", new Object[]{}, locale);
        var messageSubject = messageSource.getMessage("profile.email.edit.subject", new Object[]{}, locale);
        message.setSubject(messageSubject);
        message.setText(messageText + applicationHost + "/profile/change/" + token);
        mailSender.send(message);
    }


    private SimpleMailMessage createMailMessage(String emailAddress) {
        var message = new SimpleMailMessage();
        message.setFrom(sendAddress);
        message.setTo(emailAddress);
        return message;
    }
}

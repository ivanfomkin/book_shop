package com.github.ivanfomkin.bookshop.service.impl;

import com.github.ivanfomkin.bookshop.entity.other.MessageEntity;
import com.github.ivanfomkin.bookshop.repository.EmailMessageRepository;
import com.github.ivanfomkin.bookshop.repository.UserContactRepository;
import com.github.ivanfomkin.bookshop.service.EmailMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Slf4j
@Service
public class EmailMessageServiceImpl implements EmailMessageService {
    private final MailSender mailSender;
    private final MessageSource messageSource;
    private final UserContactRepository userContactRepository;
    private final EmailMessageRepository emailMessageRepository;

    @Value("${spring.mail.username}")
    private String sendAddress;

    @Value("${bookshop.host}")
    private String applicationHost;

    public EmailMessageServiceImpl(MailSender mailSender, MessageSource messageSource, UserContactRepository userContactRepository, EmailMessageRepository emailMessageRepository) {
        this.mailSender = mailSender;
        this.messageSource = messageSource;
        this.userContactRepository = userContactRepository;
        this.emailMessageRepository = emailMessageRepository;
    }

    @Override
    public void sendConfirmMessage(String emailAddress, String code) {
        Locale locale = LocaleContextHolder.getLocale();
        var emailConfirmMessageText = messageSource.getMessage("profile.email.confirm.text", new Object[]{}, locale) + "\n" + code;
        var emailConfirmSubject = messageSource.getMessage("profile.email.confirm.subject", new Object[]{}, locale);
        sendMessage(emailAddress, emailConfirmMessageText, emailConfirmSubject, null);
    }

    @Override
    public void sendChangeDataMessage(String emailAddress, String token) {
        var locale = LocaleContextHolder.getLocale();
        var messageText = messageSource.getMessage("profile.email.edit.text", new Object[]{}, locale) + applicationHost + "/profile/change/" + token;
        var messageSubject = messageSource.getMessage("profile.email.edit.subject", new Object[]{}, locale);
        sendMessage(emailAddress, messageText, messageSubject, null);
    }

    @Override
    public void sendMessage(String emailAddress, String messageText, String messageSubject, String name) {
        var message = new SimpleMailMessage();
        message.setFrom(sendAddress);
        message.setTo(emailAddress);
        message.setText(messageText);
        message.setSubject(messageSubject);
        mailSender.send(message);
        saveMessage(message, name);
    }

    private void saveMessage(SimpleMailMessage message, String name) {
        List<MessageEntity> messageEntities = new ArrayList<>(Objects.requireNonNull(message.getTo()).length);
        for (String email : message.getTo()) {
            var entity = new MessageEntity();
            var userContact = userContactRepository.findByContact(email);
            var user = userContact == null ? null : userContact.getUser();
            entity.setEmail(email);
            entity.setText(message.getText());
            entity.setSubject(message.getSubject());
            entity.setUser(user);
            if (name == null) {
                if (user != null) {
                    entity.setName(user.getName());
                }
            } else {
                entity.setName(name);
            }
            messageEntities.add(entity);
        }
        emailMessageRepository.saveAll(messageEntities);
    }
}

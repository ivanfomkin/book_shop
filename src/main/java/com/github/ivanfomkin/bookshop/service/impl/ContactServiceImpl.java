package com.github.ivanfomkin.bookshop.service.impl;

import com.github.ivanfomkin.bookshop.dto.contact.ContactRequestDto;
import com.github.ivanfomkin.bookshop.service.ContactService;
import com.github.ivanfomkin.bookshop.service.EmailMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ContactServiceImpl implements ContactService {
    private final EmailMessageService emailMessageService;

    public ContactServiceImpl(EmailMessageService emailMessageService) {
        this.emailMessageService = emailMessageService;
    }

    @Value("${bookshop.support.email}")
    private String adminEmail;

    @Override
    public void handleContactRequest(ContactRequestDto contactRequestDto) {
        emailMessageService.sendMessage(adminEmail, "Новое обращение в службу поддержки: \n" + contactRequestDto.toString(), "Обращение в службу поддержки", contactRequestDto.getName());
    }
}

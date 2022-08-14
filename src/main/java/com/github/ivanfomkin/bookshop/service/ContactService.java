package com.github.ivanfomkin.bookshop.service;


import com.github.ivanfomkin.bookshop.dto.contact.ContactRequestDto;

public interface ContactService {
    void handleContactRequest(ContactRequestDto contactRequestDto);
}

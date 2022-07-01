package com.github.ivanfomkin.bookshop.service;

public interface EmailMessageService {
    void sendMessage(String emailAddress, String code);
}

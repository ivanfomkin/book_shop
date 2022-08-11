package com.github.ivanfomkin.bookshop.service;

public interface EmailMessageService {
    void sendConfirmMessage(String emailAddress, String code);
    void sendChangeDataMessage(String emailAddress, String token);
}

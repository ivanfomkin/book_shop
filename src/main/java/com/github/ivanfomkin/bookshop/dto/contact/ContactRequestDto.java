package com.github.ivanfomkin.bookshop.dto.contact;

import lombok.Data;

@Data
public class ContactRequestDto {
    private String mail;
    private String name;
    private String topic;
    private String message;

    @Override
    public String toString() {
        return "Обращение в службу поддержки: " +
                "email: " + mail + '\n' +
                "Имя: " + name + '\n' +
                "Тема: " + topic + '\n' +
                "Текст: " + message + '\n';
    }
}

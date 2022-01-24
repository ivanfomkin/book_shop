package com.example.MyBookShopApp.dto.security;

import lombok.Data;

@Data
public class ContactConfirmationRequestDto {
    private String contact;
    private String code;
}

package com.example.MyBookShopApp.dto.security;

import lombok.Data;

@Data
public class RegistrationFormDto {
    private String name;
    private String email;
    private String phone;
    private String password;
}

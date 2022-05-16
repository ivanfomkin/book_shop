package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.dto.security.ContactConfirmationRequestDto;
import com.example.MyBookShopApp.dto.security.ContactConfirmationResponse;
import com.example.MyBookShopApp.dto.security.RegistrationFormDto;
import com.example.MyBookShopApp.entity.user.UserEntity;

import javax.servlet.http.HttpSession;
import java.util.Map;

public interface UserService {
    UserEntity getUserBySession(HttpSession session);

    boolean isAuthorized(HttpSession httpSession);

    void registerNewUser(RegistrationFormDto formDto, HttpSession httpSession);

    Map<String, Object> login(ContactConfirmationRequestDto dto);

    ContactConfirmationResponse jwtLogin(ContactConfirmationRequestDto dto);

    Object getCurrentUser();
}

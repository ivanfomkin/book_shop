package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.dto.security.ContactConfirmationRequestDto;
import com.example.MyBookShopApp.dto.security.ContactConfirmationResponse;
import com.example.MyBookShopApp.dto.security.RegistrationFormDto;
import com.example.MyBookShopApp.dto.user.UserDto;
import com.example.MyBookShopApp.entity.user.UserEntity;

import java.util.Map;

public interface UserService {

    void registerNewUser(RegistrationFormDto formDto);

    UserEntity registerOAuthUser(Map<String, Object> attributes, String authorizedClientRegistrationId);

    ContactConfirmationResponse jwtLogin(ContactConfirmationRequestDto dto);

    UserEntity getCurrentUser();

    UserDto getCurrentUserInfo();
}

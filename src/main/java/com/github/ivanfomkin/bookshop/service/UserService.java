package com.github.ivanfomkin.bookshop.service;

import com.github.ivanfomkin.bookshop.dto.security.ContactConfirmationRequestDto;
import com.github.ivanfomkin.bookshop.dto.security.ContactConfirmationResponse;
import com.github.ivanfomkin.bookshop.dto.security.RegistrationFormDto;
import com.github.ivanfomkin.bookshop.dto.user.UserDto;
import com.github.ivanfomkin.bookshop.entity.user.UserEntity;

import java.util.Map;

public interface UserService {

    UserEntity registerNewUser(RegistrationFormDto formDto);

    UserEntity registerOAuthUser(Map<String, Object> attributes, String authorizedClientRegistrationId);

    ContactConfirmationResponse jwtLogin(ContactConfirmationRequestDto dto);

    UserEntity getCurrentUser();

    UserDto getCurrentUserInfo();
}

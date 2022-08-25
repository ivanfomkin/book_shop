package com.github.ivanfomkin.bookshop.service;

import com.github.ivanfomkin.bookshop.dto.CommonPageableDto;
import com.github.ivanfomkin.bookshop.dto.security.ContactConfirmationRequestDto;
import com.github.ivanfomkin.bookshop.dto.security.ContactConfirmationResponse;
import com.github.ivanfomkin.bookshop.dto.security.RegistrationFormDto;
import com.github.ivanfomkin.bookshop.dto.user.UpdateProfileDto;
import com.github.ivanfomkin.bookshop.dto.user.UserDto;
import com.github.ivanfomkin.bookshop.dto.user.UserInfoElementDto;
import com.github.ivanfomkin.bookshop.dto.user.UserPageDto;
import com.github.ivanfomkin.bookshop.entity.enums.TransactionType;
import com.github.ivanfomkin.bookshop.entity.user.UserEntity;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface UserService {

    UserEntity registerNewUser(RegistrationFormDto formDto);

    UserEntity registerOAuthUser(Map<String, Object> attributes, String authorizedClientRegistrationId);

    ContactConfirmationResponse jwtLogin(ContactConfirmationRequestDto dto);

    UserEntity getCurrentUser();

    UserDto getCurrentUserInfo();

    void updateProfile(UpdateProfileDto updateProfileDto);

    UserPageDto getUserPageDto();

    void updateUserBalance(UserEntity currentUser, Double amount, TransactionType type);

    void updateProfileConfirm(String token);

    CommonPageableDto<UserInfoElementDto> getPageableAllUsers(Pageable pageable, String searchQuery);
}

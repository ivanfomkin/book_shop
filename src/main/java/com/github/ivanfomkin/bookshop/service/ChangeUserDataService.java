package com.github.ivanfomkin.bookshop.service;

import com.github.ivanfomkin.bookshop.dto.user.UpdateProfileDto;
import com.github.ivanfomkin.bookshop.entity.user.ChangeUserDataEntity;
import com.github.ivanfomkin.bookshop.entity.user.UserEntity;

public interface ChangeUserDataService {
    ChangeUserDataEntity createChangeUserData(UserEntity user, UpdateProfileDto dto);

    ChangeUserDataEntity findChangeUserDataByToken(String token);

    void deleteChangeUserData(ChangeUserDataEntity changeDataEntity);
}

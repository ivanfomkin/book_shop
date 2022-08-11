package com.github.ivanfomkin.bookshop.service.impl;

import com.github.ivanfomkin.bookshop.dto.user.UpdateProfileDto;
import com.github.ivanfomkin.bookshop.entity.user.ChangeUserDataEntity;
import com.github.ivanfomkin.bookshop.entity.user.UserEntity;
import com.github.ivanfomkin.bookshop.exception.ChangeUserDataException;
import com.github.ivanfomkin.bookshop.repository.ChangeUserDataRepository;
import com.github.ivanfomkin.bookshop.service.ChangeUserDataService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
public class ChangeUserDataServiceImpl implements ChangeUserDataService {
    private final MessageSource messageSource;
    private final ChangeUserDataRepository repository;

    public ChangeUserDataServiceImpl(MessageSource messageSource, ChangeUserDataRepository repository) {
        this.messageSource = messageSource;
        this.repository = repository;
    }

    @Override
    public ChangeUserDataEntity createChangeUserData(UserEntity user, UpdateProfileDto dto) {
        ChangeUserDataEntity changeUserData = repository.findById_UserEntity(user);
        if (changeUserData == null) {
            changeUserData = new ChangeUserDataEntity(user, dto);
        } else {
            changeUserData.setData(dto);
        }
        return repository.save(changeUserData);
    }

    @Override
    public ChangeUserDataEntity findChangeUserDataByToken(String token) {
        return repository.findByToken(token).orElseThrow(() -> new ChangeUserDataException(messageSource.getMessage("profile.edit.exception", new Object[]{}, LocaleContextHolder.getLocale())));
    }

    @Override
    public void deleteChangeUserData(ChangeUserDataEntity changeDataEntity) {
        repository.delete(changeDataEntity);
    }
}

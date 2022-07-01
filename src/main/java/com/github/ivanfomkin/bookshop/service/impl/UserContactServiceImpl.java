package com.github.ivanfomkin.bookshop.service.impl;

import com.github.ivanfomkin.bookshop.dto.CommonResultDto;
import com.github.ivanfomkin.bookshop.dto.security.ContactConfirmationRequestDto;
import com.github.ivanfomkin.bookshop.entity.enums.ContactType;
import com.github.ivanfomkin.bookshop.entity.user.UserContactEntity;
import com.github.ivanfomkin.bookshop.repository.UserContactRepository;
import com.github.ivanfomkin.bookshop.service.CallService;
import com.github.ivanfomkin.bookshop.service.UserContactService;
import com.github.ivanfomkin.bookshop.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class UserContactServiceImpl implements UserContactService {
    @Value("${bookshop.call.expiration}")
    private Integer codeExpirationTime;
    private final CallService callService;
    private final UserContactRepository userContactRepository;

    public UserContactServiceImpl(CallService callService, UserContactRepository userContactRepository) {
        this.callService = callService;
        this.userContactRepository = userContactRepository;
    }

    @Override
    public CommonResultDto sendContactConfirmationCode(ContactConfirmationRequestDto dto, String ip) {
        UserContactEntity contactEntity;
        String code;
        if (CommonUtils.isPhoneNumber(dto.getContact())) {
            var number = CommonUtils.formatPhoneNumber(dto.getContact());
            contactEntity = userContactRepository.findByContact(number);
            code = callService.call(number, ip);
            if (contactEntity == null) {
                contactEntity = new UserContactEntity();
                contactEntity.setContact(number);
                contactEntity.setType(ContactType.PHONE);
                contactEntity.setApproved((short) 0);
            }
        } else {
            var email = dto.getContact();
            contactEntity = userContactRepository.findByContact(email);
            code = CommonUtils.generateRandomCode();
            if (contactEntity == null) {
                contactEntity = new UserContactEntity();
                contactEntity.setContact(email);
                contactEntity.setType(ContactType.EMAIL);
                contactEntity.setApproved((short) 0);
            }
        }
        contactEntity.setCode(code);
        contactEntity.setCodeTrials(0);
        contactEntity.setCodeTime(LocalDateTime.now());
        userContactRepository.save(contactEntity);
        return new CommonResultDto(true);
    }

    @Override
    public CommonResultDto approveContact(ContactConfirmationRequestDto dto) {
        CommonResultDto resultDto;
        if (!CommonUtils.isPhoneNumber(dto.getContact())) {
            return new CommonResultDto(true); //Заглушка пока нет отправки кода на email
        }
        var contactValue = !CommonUtils.isPhoneNumber(dto.getContact()) ? dto.getContact() : CommonUtils.formatPhoneNumber(dto.getContact());
        var userContactEntity = userContactRepository.findByContact(contactValue);
        var code = dto.getCode().replaceAll("\\D", "");
        if (userContactEntity.getCode().equals(code) && userContactEntity.getCodeTime().plusMinutes(codeExpirationTime).isAfter(LocalDateTime.now())) {
            resultDto = new CommonResultDto(true);
        } else {
            userContactEntity.setCodeTrials(userContactEntity.getCodeTrials() + 1);
            resultDto = new CommonResultDto(false);
        }
        return resultDto;
    }
}

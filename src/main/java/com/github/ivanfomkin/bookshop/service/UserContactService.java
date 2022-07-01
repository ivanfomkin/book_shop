package com.github.ivanfomkin.bookshop.service;

import com.github.ivanfomkin.bookshop.dto.CommonResultDto;
import com.github.ivanfomkin.bookshop.dto.security.ContactConfirmationRequestDto;

public interface UserContactService {
    CommonResultDto sendContactConfirmationCode(ContactConfirmationRequestDto dto, String ip);

    CommonResultDto approveContact(ContactConfirmationRequestDto dto);
}

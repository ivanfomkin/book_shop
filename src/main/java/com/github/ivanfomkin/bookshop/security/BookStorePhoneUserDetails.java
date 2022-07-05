package com.github.ivanfomkin.bookshop.security;

import com.github.ivanfomkin.bookshop.entity.enums.ContactType;
import com.github.ivanfomkin.bookshop.entity.user.UserContactEntity;
import com.github.ivanfomkin.bookshop.entity.user.UserEntity;

public class BookStorePhoneUserDetails extends BookStoreUserDetails {
    private final String phone;
    private final String phoneConfirmationCode;

    public BookStorePhoneUserDetails(UserEntity user) {
        super(user);
        UserContactEntity phoneContactEntity = user.getPhone();
        this.phoneConfirmationCode = phoneContactEntity.getCode();
        this.phone = phoneContactEntity.getContact();
    }

    public String getPhoneConfirmationCode() {
        return phoneConfirmationCode;
    }

    @Override
    public String getUsername() {
        return phone;
    }
}

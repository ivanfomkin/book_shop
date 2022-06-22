package com.github.ivanfomkin.bookshop.service;

import com.github.ivanfomkin.bookshop.entity.book.BookEntity;
import com.github.ivanfomkin.bookshop.entity.enums.Book2UserType;
import com.github.ivanfomkin.bookshop.entity.user.UserEntity;

public interface Book2UserService {
    void changeBookStatus(UserEntity user, String slug, Book2UserType status);

    void changeBookStatus(UserEntity user, BookEntity bookEntity, Book2UserType status);

    int getCartAmount(UserEntity user);

    int getKeptAmount(UserEntity user);
}

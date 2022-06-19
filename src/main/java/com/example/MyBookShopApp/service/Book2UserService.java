package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.entity.book.BookEntity;
import com.example.MyBookShopApp.entity.enums.Book2UserType;
import com.example.MyBookShopApp.entity.user.UserEntity;

public interface Book2UserService {
    void changeBookStatus(UserEntity user, String slug, Book2UserType status);

    void changeBookStatus(UserEntity user, BookEntity bookEntity, Book2UserType status);

    int getCartAmount(UserEntity user);

    int getKeptAmount(UserEntity user);
}

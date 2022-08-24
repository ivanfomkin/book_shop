package com.github.ivanfomkin.bookshop.service;

import com.github.ivanfomkin.bookshop.dto.user.UserBookShelfDto;
import com.github.ivanfomkin.bookshop.entity.book.BookEntity;
import com.github.ivanfomkin.bookshop.entity.enums.Book2UserType;
import com.github.ivanfomkin.bookshop.entity.user.UserEntity;

import java.util.List;

public interface Book2UserService {
    void changeBookStatus(UserEntity user, String slug, Book2UserType status);

    void changeBookStatus(UserEntity user, BookEntity bookEntity, Book2UserType status);

    int getCartAmount(UserEntity user);

    int getKeptAmount(UserEntity user);

    int getMyBookAmount(UserEntity user);

    List<UserBookShelfDto> getUserBookShelf(Integer userId);

    Integer deleteByBook2UserId(Integer bookToUserId);

    void changeBookStatus(Integer userId, String[] bookSlugs, Book2UserType paid);
}

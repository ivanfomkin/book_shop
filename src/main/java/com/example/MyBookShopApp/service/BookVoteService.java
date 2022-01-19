package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.dto.book.BookRatingInfoDto;
import com.example.MyBookShopApp.entity.book.BookEntity;
import com.example.MyBookShopApp.entity.user.UserEntity;

public interface BookVoteService {
    Object getBookRating(BookEntity bookEntity);

    BookRatingInfoDto getBookRatingDto(BookEntity book);

    void rateBook(UserEntity user, BookEntity book, short rateValue);
}

package com.github.ivanfomkin.bookshop.service;

import com.github.ivanfomkin.bookshop.dto.book.BookRatingInfoDto;
import com.github.ivanfomkin.bookshop.entity.book.BookEntity;
import com.github.ivanfomkin.bookshop.entity.user.UserEntity;

public interface BookVoteService {
    Object getBookRating(BookEntity bookEntity);

    BookRatingInfoDto getBookRatingDto(BookEntity book);

    void rateBook(UserEntity user, BookEntity book, short rateValue);
}

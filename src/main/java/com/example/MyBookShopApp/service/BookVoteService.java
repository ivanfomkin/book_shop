package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.dto.book.BookRatingInfoDto;
import com.example.MyBookShopApp.entity.book.BookEntity;

public interface BookVoteService {
    Object getBookRating(BookEntity bookEntity);

    BookRatingInfoDto getBookRatingDto(BookEntity book);
}

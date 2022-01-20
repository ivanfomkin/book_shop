package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.dto.book.review.BookReviewDto;
import com.example.MyBookShopApp.entity.book.BookEntity;

public interface BookReviewService {
    BookReviewDto getReviewDtoForBook(BookEntity book);
}

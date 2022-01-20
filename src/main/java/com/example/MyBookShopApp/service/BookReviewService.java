package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.dto.book.review.BookReviewDto;
import com.example.MyBookShopApp.entity.book.BookEntity;
import com.example.MyBookShopApp.entity.book.review.BookReviewEntity;
import com.example.MyBookShopApp.entity.user.UserEntity;

import java.util.Map;

public interface BookReviewService {
    BookReviewDto getReviewDtoForBook(BookEntity book);

    BookReviewEntity getBookReviewEntityById(int id);

    Map<String, Object> saveBookReview(BookEntity book, String text, UserEntity user);
}

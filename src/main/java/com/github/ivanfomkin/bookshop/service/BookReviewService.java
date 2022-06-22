package com.github.ivanfomkin.bookshop.service;

import com.github.ivanfomkin.bookshop.dto.CommonResultDto;
import com.github.ivanfomkin.bookshop.dto.book.review.BookReviewDto;
import com.github.ivanfomkin.bookshop.entity.book.BookEntity;
import com.github.ivanfomkin.bookshop.entity.book.review.BookReviewEntity;
import com.github.ivanfomkin.bookshop.entity.user.UserEntity;

public interface BookReviewService {
    BookReviewDto getReviewDtoForBook(BookEntity book);

    BookReviewEntity getBookReviewEntityById(int id);

    CommonResultDto saveBookReview(BookEntity book, String text, UserEntity user);
}

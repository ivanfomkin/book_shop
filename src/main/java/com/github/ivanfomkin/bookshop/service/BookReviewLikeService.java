package com.github.ivanfomkin.bookshop.service;

import com.github.ivanfomkin.bookshop.dto.book.review.BookReviewLikeRequestDto;
import com.github.ivanfomkin.bookshop.entity.user.UserEntity;

public interface BookReviewLikeService {
    boolean saveBookReviewLike(BookReviewLikeRequestDto dto, UserEntity userEntity);
}

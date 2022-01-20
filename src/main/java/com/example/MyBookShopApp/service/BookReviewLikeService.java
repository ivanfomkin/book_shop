package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.dto.book.review.BookReviewLikeRequestDto;
import com.example.MyBookShopApp.entity.user.UserEntity;

public interface BookReviewLikeService {
    void saveBookReviewLike(BookReviewLikeRequestDto dto, UserEntity userBySession);
}

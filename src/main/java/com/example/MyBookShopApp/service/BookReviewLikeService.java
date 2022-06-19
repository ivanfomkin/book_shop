package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.dto.book.review.BookReviewLikeRequestDto;
import com.example.MyBookShopApp.entity.user.UserEntity;

public interface BookReviewLikeService {
    boolean saveBookReviewLike(BookReviewLikeRequestDto dto, UserEntity userEntity);
}

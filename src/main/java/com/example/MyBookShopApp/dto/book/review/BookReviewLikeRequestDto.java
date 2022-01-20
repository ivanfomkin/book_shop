package com.example.MyBookShopApp.dto.book.review;

import lombok.Data;

@Data
public class BookReviewLikeRequestDto {
    private int reviewId;
    private short value;
}

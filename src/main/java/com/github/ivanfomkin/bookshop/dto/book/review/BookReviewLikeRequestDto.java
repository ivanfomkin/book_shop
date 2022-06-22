package com.github.ivanfomkin.bookshop.dto.book.review;

import lombok.Data;

@Data
public class BookReviewLikeRequestDto {
    private int reviewId;
    private short value;
}

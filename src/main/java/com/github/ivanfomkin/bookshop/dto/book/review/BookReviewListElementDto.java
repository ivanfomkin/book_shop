package com.github.ivanfomkin.bookshop.dto.book.review;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookReviewListElementDto {
    private String userName;
    private int reviewId;
    private LocalDateTime reviewDate;
    private short stars;
    private String text;
    private int likes;
    private int dislikes;
}

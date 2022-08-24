package com.github.ivanfomkin.bookshop.dto.review;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookReviewElementDto {
    private int id;
    private String text;
    private LocalDateTime time;
    private BookSlugInfoDto book;
    private ReviewUserInfo user;
}

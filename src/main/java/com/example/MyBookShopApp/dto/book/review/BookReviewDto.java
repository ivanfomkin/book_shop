package com.example.MyBookShopApp.dto.book.review;

import lombok.Data;

import java.util.List;

@Data
public class BookReviewDto {
    private int count;
    private int stars;
    private List<BookReviewListElementDto> reviews;
}

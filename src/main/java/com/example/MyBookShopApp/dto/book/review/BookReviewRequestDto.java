package com.example.MyBookShopApp.dto.book.review;

import lombok.Data;

@Data
public class BookReviewRequestDto {
    private String bookId;
    private String text;
}

package com.github.ivanfomkin.bookshop.dto.review;

import lombok.Data;

import java.util.List;

@Data
public class PageableBookReviewDto {
    private long total;
    private List<BookReviewElementDto> reviews;
    private long perPage;
    private long page;
}

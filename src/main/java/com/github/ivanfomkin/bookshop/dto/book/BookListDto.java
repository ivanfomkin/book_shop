package com.github.ivanfomkin.bookshop.dto.book;

import lombok.Data;

import java.util.List;

@Data
public class BookListDto {
    private long total;
    private List<BookListElement> books;
    private long perPage;
    private long page;
}

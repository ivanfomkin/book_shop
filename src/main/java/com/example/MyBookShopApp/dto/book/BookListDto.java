package com.example.MyBookShopApp.dto.book;

import lombok.Data;

import java.util.List;

@Data
public class BookListDto {
    private long count;
    private List<BookListElement> books;
}

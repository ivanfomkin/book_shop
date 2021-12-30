package com.example.MyBookShopApp.dto.book;

import com.example.MyBookShopApp.dto.Dto;
import lombok.Data;

import java.util.List;

@Data
public class BookListDto implements Dto {
    private long count;
    private List<BookDto> books;
}

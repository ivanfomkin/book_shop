package com.example.MyBookShopApp.dto.book;

import com.example.MyBookShopApp.dto.Dto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BookListDto implements Dto {
    private long count;
    private List<BookDto> books;
}

package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.dto.book.BookListDto;
import com.example.MyBookShopApp.entity.book.BookEntity;

import java.util.List;

public interface BookService {
    List<BookEntity> getBooksData();

    BookListDto getPageableRecommendedBooks(Integer offset, Integer limit);
}

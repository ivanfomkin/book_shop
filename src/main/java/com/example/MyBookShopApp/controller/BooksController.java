package com.example.MyBookShopApp.controller;

import com.example.MyBookShopApp.model.Book;

import java.util.List;

public interface BooksController {
    List<Book> recommendedBooks();

    String recentBooks();

    String popularBooks();
}

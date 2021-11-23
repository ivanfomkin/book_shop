package com.example.MyBookShopApp.controller;

import com.example.MyBookShopApp.model.Book;

import java.util.List;

public interface MainPageController {
    List<Book> recommendedBooks();

    String mainPage();
}

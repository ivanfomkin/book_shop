package com.example.MyBookShopApp.controller.impl;

import com.example.MyBookShopApp.controller.MainPageController;
import com.example.MyBookShopApp.model.Book;
import com.example.MyBookShopApp.service.BookService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@Controller
public class MainPageControllerImpl implements MainPageController {

    private final BookService bookService;

    public MainPageControllerImpl(BookService bookService) {
        this.bookService = bookService;
    }

    @ModelAttribute("recommendedBooks")
    public List<Book> recommendedBooks() {
        return bookService.getBooksData();
    }

    @GetMapping
    public String mainPage() {
        return "index";
    }
}

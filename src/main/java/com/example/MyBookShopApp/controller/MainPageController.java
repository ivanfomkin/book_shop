package com.example.MyBookShopApp.controller;

import com.example.MyBookShopApp.dto.book.BookListDto;
import com.example.MyBookShopApp.service.BookService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class MainPageController {

    private final BookService bookService;

    public MainPageController(BookService bookService) {
        this.bookService = bookService;
    }

    @ModelAttribute("bookList")
    public BookListDto recommendedBooks() {
        return bookService.getPageableRecommendedBooks(0, 20);
    }

    @GetMapping
    public String mainPage(Model model) {
        return "index";
    }
}

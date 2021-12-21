package com.example.MyBookShopApp.controller;

import com.example.MyBookShopApp.service.BookService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/books")
public class BooksController {
    private final BookService bookService;

    public BooksController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/recent")
    public String recentBooks(Model model) {
        model.addAttribute("bookList", bookService.getPageableRecentBooks(0, 20));
        return "books/recent";
    }

    @GetMapping("/popular")
    public String popularBooks(Model model) {
        model.addAttribute("bookList", bookService.getPageablePopularBooks(0, 20));
        return "books/popular";
    }
}

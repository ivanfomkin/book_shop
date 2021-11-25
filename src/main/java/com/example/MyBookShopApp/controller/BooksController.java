package com.example.MyBookShopApp.controller;

import com.example.MyBookShopApp.model.Book;
import com.example.MyBookShopApp.service.BookService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/books")
public class BooksController {
    private final BookService bookService;

    public BooksController(BookService bookService) {
        this.bookService = bookService;
    }

    @ModelAttribute("recommendedBooks")
    public List<Book> recommendedBooks() {
        return bookService.getBooksData();
    }

    @GetMapping("/recent")
    public String recentBooks() {
        return "books/recent";
    }

    @GetMapping("/popular")
    public String popularBooks() {
        return "books/popular";
    }
}

package com.example.MyBookShopApp.controller.rest;

import com.example.MyBookShopApp.dto.book.BookListDto;
import com.example.MyBookShopApp.service.BookService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/books")
public class BookRestController {
    private final BookService bookService;

    public BookRestController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/recommended")
    public BookListDto getRecommendedBooks(@RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset,
                                           @RequestParam(value = "limit", required = false, defaultValue = "20") Integer limit) {
        return bookService.getPageableRecommendedBooks(offset, limit);
    }
}

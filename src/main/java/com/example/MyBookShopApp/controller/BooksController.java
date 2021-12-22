package com.example.MyBookShopApp.controller;

import com.example.MyBookShopApp.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequestMapping("/books")
public class BooksController {
    private final BookService bookService;

    public BooksController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/recent")
    public String recentBooks(Model model,
                              @RequestParam(name = "offset", required = false, defaultValue = "0") Integer offset,
                              @RequestParam(name = "limit", required = false, defaultValue = "20") Integer limit,
                              @RequestParam(name = "from", required = false, defaultValue = "0") String fromDate,
                              @RequestParam(name = "to", required = false, defaultValue = "0") String toDate) {
        model.addAttribute("bookList", bookService.getPageableRecentBooks(offset, limit));
        return "books/recent";
    }

    @GetMapping("/popular")
    public String popularBooks(Model model) {
        model.addAttribute("bookList", bookService.getPageablePopularBooks(0, 20));
        return "books/popular";
    }
}

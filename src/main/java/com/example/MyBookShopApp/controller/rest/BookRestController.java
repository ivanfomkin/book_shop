package com.example.MyBookShopApp.controller.rest;

import com.example.MyBookShopApp.dto.book.BookListDto;
import com.example.MyBookShopApp.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("api/books")
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

    @GetMapping("/recent")
    public BookListDto recentBooks(@RequestParam(name = "offset", required = false, defaultValue = "0") Integer offset,
                                   @RequestParam(name = "limit", required = false, defaultValue = "20") Integer limit,
                                   @RequestParam(name = "from", required = false, defaultValue = "0") String fromDate,
                                   @RequestParam(name = "to", required = false, defaultValue = "0") String toDate) {
        return bookService.getPageableRecentBooks(offset, limit, fromDate, toDate);
    }

}

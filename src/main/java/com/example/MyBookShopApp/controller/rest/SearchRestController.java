package com.example.MyBookShopApp.controller.rest;

import com.example.MyBookShopApp.dto.book.BookListDto;
import com.example.MyBookShopApp.dto.search.SearchDto;
import com.example.MyBookShopApp.service.BookService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/search")
public class SearchRestController {
    private final BookService bookService;

    public SearchRestController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping(value = {"/", "/{searchWord}"})
    public BookListDto searchResults(@RequestParam(name = "offset", required = false, defaultValue = "0") Integer offset,
                                     @RequestParam(name = "limit", required = false, defaultValue = "20") Integer limit,
                                     @PathVariable(name = "searchWord", required = false) SearchDto searchDto) {
        return bookService.getPageableBooksByTitle(offset, limit, searchDto.searchQuery());
    }
}

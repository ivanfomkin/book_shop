package com.github.ivanfomkin.bookshop.controller.rest;

import com.github.ivanfomkin.bookshop.dto.book.BookListDto;
import com.github.ivanfomkin.bookshop.dto.search.SearchDto;
import com.github.ivanfomkin.bookshop.service.BookService;
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

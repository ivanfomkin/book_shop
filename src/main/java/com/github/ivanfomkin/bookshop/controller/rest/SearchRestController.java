package com.github.ivanfomkin.bookshop.controller.rest;

import com.github.ivanfomkin.bookshop.dto.CommonPageableDto;
import com.github.ivanfomkin.bookshop.dto.book.BookListElement;
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
    public CommonPageableDto<BookListElement> searchResults(@RequestParam(name = "offset", required = false, defaultValue = "0") Integer offset,
                                                            @RequestParam(name = "limit", required = false, defaultValue = "20") Integer limit,
                                                            @CookieValue(value = "cartContent", required = false, defaultValue = "") String cartCookie,
                                                            @CookieValue(value = "keptContent", required = false, defaultValue = "") String keptCookie,
                                                            @PathVariable(name = "searchWord", required = false) SearchDto searchDto) {
        return bookService.getPageableBooksByTitle(offset, limit, searchDto.searchQuery(), cartCookie, keptCookie);
    }
}

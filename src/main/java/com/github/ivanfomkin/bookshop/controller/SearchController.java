package com.github.ivanfomkin.bookshop.controller;

import com.github.ivanfomkin.bookshop.dto.CommonPageableDto;
import com.github.ivanfomkin.bookshop.dto.book.BookListElement;
import com.github.ivanfomkin.bookshop.dto.search.SearchDto;
import com.github.ivanfomkin.bookshop.exception.EmptySearchQueryException;
import com.github.ivanfomkin.bookshop.service.Book2UserService;
import com.github.ivanfomkin.bookshop.service.BookService;
import com.github.ivanfomkin.bookshop.service.CookieService;
import com.github.ivanfomkin.bookshop.service.UserService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class SearchController extends ModelAttributeController {
    private final BookService bookService;
    private final MessageSource messageSource;

    public SearchController(UserService userService, Book2UserService book2UserService, BookService bookService, CookieService cookieService, MessageSource messageSource) {
        super(userService, cookieService, book2UserService);
        this.bookService = bookService;
        this.messageSource = messageSource;
    }

    @ModelAttribute("searchResults")
    public CommonPageableDto<BookListElement> searchResults() {
        return new CommonPageableDto<>();
    }

    @GetMapping(value = {"/search", "/search/{searchWord}"})
    public String searchPage(Model model,
                             @RequestParam(name = "offset", required = false, defaultValue = "0") Integer offset,
                             @RequestParam(name = "limit", required = false, defaultValue = "20") Integer limit,
                             @CookieValue(value = "cartContent", required = false, defaultValue = "") String cartCookie,
                             @CookieValue(value = "keptContent", required = false, defaultValue = "") String keptCookie,
                             @PathVariable(name = "searchWord", required = false) SearchDto searchDto) {
        if (searchDto != null && !searchDto.searchQuery().isBlank()) {
            model.addAttribute("searchDto", searchDto);
            model.addAttribute("bookList", bookService.getPageableBooksByTitle(offset, limit, searchDto.searchQuery(), cartCookie, keptCookie));
            return "search/index";
        } else {
            String errorMessage = messageSource.getMessage("search.error", new Object[]{}, LocaleContextHolder.getLocale());
            throw new EmptySearchQueryException(errorMessage);
        }
    }
}

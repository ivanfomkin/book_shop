package com.github.ivanfomkin.bookshop.controller;

import com.github.ivanfomkin.bookshop.dto.book.BookListDto;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

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
    public BookListDto searchResults() {
        return new BookListDto();
    }

    @GetMapping(value = {"/search", "/search/{searchWord}"})
    public String searchPage(Model model,
                             @RequestParam(name = "offset", required = false, defaultValue = "0") Integer offset,
                             @RequestParam(name = "limit", required = false, defaultValue = "20") Integer limit,
                             @PathVariable(name = "searchWord", required = false) SearchDto searchDto) {
        if (searchDto != null && !searchDto.searchQuery().isBlank()) {
            model.addAttribute("searchDto", searchDto);
            model.addAttribute("bookList", bookService.getPageableBooksByTitle(offset, limit, searchDto.searchQuery()));
            return "search/index";
        } else {
            String errorMessage = messageSource.getMessage("search.error", new Object[]{}, LocaleContextHolder.getLocale());
            throw new EmptySearchQueryException(errorMessage);
        }
    }
}

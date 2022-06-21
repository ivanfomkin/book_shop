package com.example.MyBookShopApp.controller;

import com.example.MyBookShopApp.dto.book.BookListDto;
import com.example.MyBookShopApp.dto.search.SearchDto;
import com.example.MyBookShopApp.exception.EmptySearchQueryException;
import com.example.MyBookShopApp.service.Book2UserService;
import com.example.MyBookShopApp.service.BookService;
import com.example.MyBookShopApp.service.CookieService;
import com.example.MyBookShopApp.service.UserService;
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
        if (searchDto != null && !searchDto.searchQuery().isEmpty()) {
            model.addAttribute("searchDto", searchDto);
            model.addAttribute("bookList", bookService.getPageableBooksByTitle(offset, limit, searchDto.searchQuery()));
            return "search/index";
        } else {
            String errorMessage = messageSource.getMessage("search.error", new Object[]{}, LocaleContextHolder.getLocale());
            throw new EmptySearchQueryException(errorMessage);
        }
    }
}

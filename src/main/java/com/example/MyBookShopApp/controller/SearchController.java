package com.example.MyBookShopApp.controller;

import com.example.MyBookShopApp.dto.book.BookListDto;
import com.example.MyBookShopApp.dto.search.SearchDto;
import com.example.MyBookShopApp.exception.EmptySearchQueryException;
import com.example.MyBookShopApp.service.Book2UserService;
import com.example.MyBookShopApp.service.BookService;
import com.example.MyBookShopApp.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

@Controller
public class SearchController {
    private final BookService bookService;
    private final UserService userService;
    private final Book2UserService book2UserService;

    public SearchController(BookService bookService, UserService userService, Book2UserService book2UserService) {
        this.bookService = bookService;
        this.userService = userService;
        this.book2UserService = book2UserService;
    }

    @ModelAttribute("searchDto")
    public SearchDto searchWord() {
        return new SearchDto();
    }

    @ModelAttribute("searchResults")
    public BookListDto searchResults() {
        return new BookListDto();
    }

    @ModelAttribute("cartAmount")
    public int cartAmount(HttpSession httpSession) {
        return book2UserService.getCartAmount(userService.getUserBySession(httpSession));
    }

    @ModelAttribute("keptAmount")
    public int keptAmount(HttpSession httpSession) {
        return book2UserService.getKeptAmount(userService.getUserBySession(httpSession));
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
            throw new EmptySearchQueryException("Строка поиска не может быть пустой"); // TODO: 10.01.2022 Передавать сюда локализованное сообщение
        }
    }
}

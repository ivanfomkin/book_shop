package com.github.ivanfomkin.bookshop.controller;

import com.github.ivanfomkin.bookshop.service.Book2UserService;
import com.github.ivanfomkin.bookshop.service.BookService;
import com.github.ivanfomkin.bookshop.service.CookieService;
import com.github.ivanfomkin.bookshop.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/tags")
public class TagController extends ModelAttributeController{
    private final BookService bookService;

    public TagController(UserService userService, CookieService cookieService, Book2UserService book2UserService, BookService bookService) {
        super(userService,cookieService, book2UserService);
        this.bookService = bookService;
    }

    @GetMapping("/{tag}")
    public String tagPage(Model model,
                          @RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset,
                          @RequestParam(value = "limit", required = false, defaultValue = "20") Integer limit,
                          @PathVariable(required = false) String tag) {
        model.addAttribute("bookList", bookService.getPageableBooksByTag(offset, limit, tag));
        model.addAttribute("tagName", tag);
        return "tags/index";
    }
}

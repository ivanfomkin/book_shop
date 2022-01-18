package com.example.MyBookShopApp.controller;

import com.example.MyBookShopApp.dto.search.SearchDto;
import com.example.MyBookShopApp.service.Book2UserService;
import com.example.MyBookShopApp.service.BookService;
import com.example.MyBookShopApp.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/tags")
public class TagController {
    private final BookService bookService;
    private final UserService userService;
    private final Book2UserService book2UserService;

    public TagController(BookService bookService, UserService userService, Book2UserService book2UserService) {
        this.bookService = bookService;
        this.userService = userService;
        this.book2UserService = book2UserService;
    }

    @ModelAttribute("searchDto")
    public SearchDto searchWord() {
        return new SearchDto();
    }

    @ModelAttribute("cartAmount")
    public int cartAmount(HttpSession httpSession) {
        return book2UserService.getCartAmount(userService.getUserBySession(httpSession));
    }

    @ModelAttribute("keptAmount")
    public int keptAmount(HttpSession httpSession) {
        return book2UserService.getKeptAmount(userService.getUserBySession(httpSession));
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

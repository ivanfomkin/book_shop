package com.example.MyBookShopApp.controller;

import com.example.MyBookShopApp.dto.search.SearchDto;
import com.example.MyBookShopApp.service.BookService;
import com.example.MyBookShopApp.service.CartService;
import com.example.MyBookShopApp.service.KeptService;
import com.example.MyBookShopApp.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/tags")
public class TagController {
    private final BookService bookService;
    private final CartService cartService;
    private final UserService userService;
    private final KeptService keptService;

    public TagController(BookService bookService, CartService cartService, UserService userService, KeptService keptService) {
        this.bookService = bookService;
        this.cartService = cartService;
        this.userService = userService;
        this.keptService = keptService;
    }

    @ModelAttribute("searchDto")
    public SearchDto searchWord() {
        return new SearchDto();
    }

    @ModelAttribute("cartAmount")
    public int cartAmount(HttpSession httpSession) {
        return cartService.getCartAmount(userService.getUserBySession(httpSession));
    }

    @ModelAttribute("keptAmount")
    public int keptAmount(HttpSession httpSession) {
        return keptService.getKeptAmount(userService.getUserBySession(httpSession));
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

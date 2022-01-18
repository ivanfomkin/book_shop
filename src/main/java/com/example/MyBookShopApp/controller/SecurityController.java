package com.example.MyBookShopApp.controller;

import com.example.MyBookShopApp.dto.search.SearchDto;
import com.example.MyBookShopApp.service.CartService;
import com.example.MyBookShopApp.service.KeptService;
import com.example.MyBookShopApp.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpSession;

@Controller
public class SecurityController {

    private final UserService userService;
    private final CartService cartService;
    private final KeptService keptService;

    public SecurityController(UserService userService, CartService cartService, KeptService keptService) {
        this.userService = userService;
        this.cartService = cartService;
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

    @GetMapping("/signin")

    public String signInPage() {
        return "signin";
    }
}

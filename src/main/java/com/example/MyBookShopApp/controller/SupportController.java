package com.example.MyBookShopApp.controller;

import com.example.MyBookShopApp.service.Book2UserService;
import com.example.MyBookShopApp.service.CookieService;
import com.example.MyBookShopApp.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SupportController extends ModelAttributeController {

    public SupportController(UserService userService, CookieService cookieService, Book2UserService book2UserService) {
        super(userService, cookieService, book2UserService);
    }

    @GetMapping("/faq")
    public String faqPage() {
        return "faq";
    }
}

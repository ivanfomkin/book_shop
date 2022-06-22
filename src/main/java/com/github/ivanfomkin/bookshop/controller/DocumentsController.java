package com.github.ivanfomkin.bookshop.controller;

import com.github.ivanfomkin.bookshop.service.Book2UserService;
import com.github.ivanfomkin.bookshop.service.CookieService;
import com.github.ivanfomkin.bookshop.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DocumentsController extends ModelAttributeController{

    public DocumentsController(UserService userService, CookieService cookieService, Book2UserService book2UserService) {
        super(userService, cookieService,book2UserService);
    }

    @GetMapping("/documents")
    public String documentPage() {
        return "documents/index";
    }
}

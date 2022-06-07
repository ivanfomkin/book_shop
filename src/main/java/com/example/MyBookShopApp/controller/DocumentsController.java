package com.example.MyBookShopApp.controller;

import com.example.MyBookShopApp.service.Book2UserService;
import com.example.MyBookShopApp.service.CookieService;
import com.example.MyBookShopApp.service.UserService;
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

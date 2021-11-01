package com.example.MyBookShopApp.controller.impl;

import com.example.MyBookShopApp.controller.AuthorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/authors")
public class AuthorControllerImpl implements AuthorController {

    @GetMapping
    public String authors() {
        return "authors/index";
    }
}

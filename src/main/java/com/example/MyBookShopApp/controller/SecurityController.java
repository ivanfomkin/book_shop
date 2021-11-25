package com.example.MyBookShopApp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SecurityController {
    @GetMapping("/signin")

    public String signInPage() {
        return "signin";
    }
}

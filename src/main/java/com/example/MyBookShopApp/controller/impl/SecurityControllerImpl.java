package com.example.MyBookShopApp.controller.impl;

import com.example.MyBookShopApp.controller.SecurityController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SecurityControllerImpl implements SecurityController {
    @GetMapping("/signin")

    @Override
    public String signInPage() {
        return "signin";
    }
}

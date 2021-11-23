package com.example.MyBookShopApp.controller.impl;

import com.example.MyBookShopApp.controller.AboutController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AboutControllerImpl implements AboutController {
    @GetMapping("/about")
    public String aboutPage() {
        return "/about";
    }
}

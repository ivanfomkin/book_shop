package com.example.MyBookShopApp.controller.impl;

import com.example.MyBookShopApp.controller.SupportController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SupportControllerImpl implements SupportController {
    @GetMapping("/faq")
    public String faqPage() {
        return null;
    }
}

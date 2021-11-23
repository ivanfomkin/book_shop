package com.example.MyBookShopApp.controller.impl;

import com.example.MyBookShopApp.controller.ContactController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ContactControllerImpl implements ContactController {
    @GetMapping("/contacts")
    public String contactPage() {
        return "contacts";
    }
}

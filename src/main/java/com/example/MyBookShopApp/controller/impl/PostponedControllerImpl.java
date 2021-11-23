package com.example.MyBookShopApp.controller.impl;

import com.example.MyBookShopApp.controller.PostponedController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/postponed")
public class PostponedControllerImpl implements PostponedController {

    @GetMapping
    public String postponedPage() {
        return "postponed";
    }
}

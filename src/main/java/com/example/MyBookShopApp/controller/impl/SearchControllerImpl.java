package com.example.MyBookShopApp.controller.impl;

import com.example.MyBookShopApp.controller.SearchController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SearchControllerImpl implements SearchController {
    @GetMapping("/search")
    public String searchPage() {
        return "search/index";
    }
}

package com.example.MyBookShopApp.controller.impl;

import com.example.MyBookShopApp.controller.MainPageController;
import com.example.MyBookShopApp.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/bookshop")
public class MainPageControllerImpl implements MainPageController {

    private final BookService bookServiceJdbcImpl;

    @Autowired
    public MainPageControllerImpl(BookService bookServiceJdbcImpl) {
        this.bookServiceJdbcImpl = bookServiceJdbcImpl;
    }

    @GetMapping("/main")
    public String mainPage(Model model) {
        model.addAttribute("bookData", bookServiceJdbcImpl.getBooksData());
        return "index";
    }
}

package com.example.MyBookShopApp.controller.impl;

import com.example.MyBookShopApp.controller.AuthorsController;
import com.example.MyBookShopApp.model.Author;
import com.example.MyBookShopApp.service.AuthorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/authors")
public class AuthorsControllerImpl implements AuthorsController {

    private final AuthorService authorService;

    public AuthorsControllerImpl(AuthorService authorService) {
        this.authorService = authorService;
    }

    @ModelAttribute("authorsMap")
    public Map<String, List<Author>> getAuthors() {
        return authorService.getAuthorsMap();
    }

    @GetMapping
    public String authorsPage(Model model) {
        return "authors/index";
    }
}

package com.example.MyBookShopApp.controller;

import com.example.MyBookShopApp.dto.search.SearchDto;
import com.example.MyBookShopApp.entity.author.AuthorEntity;
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
public class AuthorsController {

    private final AuthorService authorService;

    public AuthorsController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @ModelAttribute("searchDto")
    public SearchDto searchWord() {
        return new SearchDto();
    }

    @ModelAttribute("authorsMap")
    public Map<String, List<AuthorEntity>> getAuthors() {
        return authorService.getAuthorsMap();
    }

    @GetMapping
    public String authorsPage(Model model) {
        return "authors/index";
    }
}

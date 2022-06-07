package com.example.MyBookShopApp.controller;

import com.example.MyBookShopApp.entity.author.AuthorEntity;
import com.example.MyBookShopApp.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/authors")
public class AuthorsController extends ModelAttributeController{

    private final BookService bookService;
    private final AuthorService authorService;

    public AuthorsController(UserService userService, CookieService cookieService, Book2UserService book2UserService, BookService bookService, AuthorService authorService) {
        super(userService, cookieService,book2UserService);
        this.bookService = bookService;
        this.authorService = authorService;
    }

    @ModelAttribute("authorsMap")
    public Map<String, List<AuthorEntity>> getAuthors() {
        return authorService.getAuthorsMap();
    }

    @GetMapping
    public String allAuthors() {
        return "authors/index";
    }

    @GetMapping("/{slug}")
    public String authorPage(Model model,
                             @RequestParam(name = "offset", required = false, defaultValue = "0") Integer offset,
                             @RequestParam(name = "limit", required = false, defaultValue = "20") Integer limit,
                             @PathVariable String slug) {
        var author = authorService.getAuthorBySlug(slug);
        model.addAttribute("author", author);
        model.addAttribute("bookList", bookService.getPageableBooksByAuthor(offset, limit, author));
        return "authors/slug";
    }
}

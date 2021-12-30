package com.example.MyBookShopApp.controller;

import com.example.MyBookShopApp.dto.search.SearchDto;
import com.example.MyBookShopApp.entity.author.AuthorEntity;
import com.example.MyBookShopApp.service.AuthorService;
import com.example.MyBookShopApp.service.BookService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/authors")
public class AuthorsController {

    private final AuthorService authorService;
    private final BookService bookService;

    public AuthorsController(AuthorService authorService, BookService bookService) {
        this.authorService = authorService;
        this.bookService = bookService;
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

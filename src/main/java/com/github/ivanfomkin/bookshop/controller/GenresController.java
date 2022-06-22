package com.github.ivanfomkin.bookshop.controller;

import com.github.ivanfomkin.bookshop.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/genres")
public class GenresController extends ModelAttributeController {

    private final BookService bookService;
    private final GenreService genreService;

    public GenresController(UserService userService, CookieService cookieService, Book2UserService book2UserService, BookService bookService, GenreService genreService) {
        super(userService, cookieService, book2UserService);
        this.bookService = bookService;
        this.genreService = genreService;
    }

    @GetMapping
    public String genresPage(Model model) {
        model.addAttribute("genreList", genreService.getAllGenres());
        return "genres/index";
    }

    @GetMapping("/{slug}")
    public String booksByGenre(Model model,
                               @RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset,
                               @RequestParam(value = "limit", required = false, defaultValue = "20") Integer limit,
                               @PathVariable String slug) {
        var genre = genreService.getGenreBySlug(slug);
        model.addAttribute("genreName", genre.getName());
        model.addAttribute("refreshId", slug);
        model.addAttribute("bookList", bookService.getPageableBooksByGenre(offset, limit, genre));
        return "genres/slug";
    }
}

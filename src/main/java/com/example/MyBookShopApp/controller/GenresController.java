package com.example.MyBookShopApp.controller;

import com.example.MyBookShopApp.dto.search.SearchDto;
import com.example.MyBookShopApp.service.BookService;
import com.example.MyBookShopApp.service.GenreService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/genres")
public class GenresController {

    private final GenreService genreService;
    private final BookService bookService;

    public GenresController(GenreService genreService, BookService bookService) {
        this.genreService = genreService;
        this.bookService = bookService;
    }

    @ModelAttribute("searchDto")
    public SearchDto searchWord() {
        return new SearchDto();
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

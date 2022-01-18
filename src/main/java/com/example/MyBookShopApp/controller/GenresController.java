package com.example.MyBookShopApp.controller;

import com.example.MyBookShopApp.dto.search.SearchDto;
import com.example.MyBookShopApp.service.Book2UserService;
import com.example.MyBookShopApp.service.BookService;
import com.example.MyBookShopApp.service.GenreService;
import com.example.MyBookShopApp.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/genres")
public class GenresController {

    private final BookService bookService;
    private final UserService userService;
    private final GenreService genreService;
    private final Book2UserService book2UserService;

    public GenresController(BookService bookService, UserService userService, GenreService genreService, Book2UserService book2UserService) {
        this.bookService = bookService;
        this.userService = userService;
        this.genreService = genreService;
        this.book2UserService = book2UserService;
    }

    @ModelAttribute("searchDto")
    public SearchDto searchWord() {
        return new SearchDto();
    }

    @ModelAttribute("cartAmount")
    public int cartAmount(HttpSession httpSession) {
        return book2UserService.getCartAmount(userService.getUserBySession(httpSession));
    }

    @ModelAttribute("keptAmount")
    public int keptAmount(HttpSession httpSession) {
        return book2UserService.getKeptAmount(userService.getUserBySession(httpSession));
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

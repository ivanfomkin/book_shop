package com.example.MyBookShopApp.controller;

import com.example.MyBookShopApp.dto.search.SearchDto;
import com.example.MyBookShopApp.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/genres")
public class GenresController {

    private final GenreService genreService;
    private final BookService bookService;
    private final UserService userService;
    private final CartService cartService;
    private final KeptService keptService;

    public GenresController(GenreService genreService, BookService bookService, UserService userService, CartService cartService, KeptService keptService) {
        this.genreService = genreService;
        this.bookService = bookService;
        this.userService = userService;
        this.cartService = cartService;
        this.keptService = keptService;
    }

    @ModelAttribute("searchDto")
    public SearchDto searchWord() {
        return new SearchDto();
    }

    @ModelAttribute("cartAmount")
    public int cartAmount(HttpSession httpSession) {
        return cartService.getCartAmount(userService.getUserBySession(httpSession));
    }

    @ModelAttribute("keptAmount")
    public int keptAmount(HttpSession httpSession) {
        return keptService.getKeptAmount(userService.getUserBySession(httpSession));
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

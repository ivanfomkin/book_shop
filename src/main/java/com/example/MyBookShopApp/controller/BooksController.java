package com.example.MyBookShopApp.controller;

import com.example.MyBookShopApp.dto.search.SearchDto;
import com.example.MyBookShopApp.service.AuthorService;
import com.example.MyBookShopApp.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/books")
public class BooksController {
    private final BookService bookService;
    private final AuthorService authorService;

    public BooksController(BookService bookService, AuthorService authorService) {
        this.bookService = bookService;
        this.authorService = authorService;
    }

    @ModelAttribute("searchDto")
    public SearchDto searchWord() {
        return new SearchDto();
    }

    @GetMapping("/recent")
    public String recentBooks(Model model,
                              @RequestParam(name = "offset", required = false, defaultValue = "0") Integer offset,
                              @RequestParam(name = "limit", required = false, defaultValue = "20") Integer limit,
                              @RequestParam(name = "from", required = false, defaultValue = "0") String fromDate,
                              @RequestParam(name = "to", required = false, defaultValue = "0") String toDate) {
        model.addAttribute("bookList", bookService.getPageableRecentBooks(offset, limit));
        return "books/recent";
    }

    @GetMapping("/popular")
    public String popularBooks(Model model,
                               @RequestParam(name = "offset", required = false, defaultValue = "0") Integer offset,
                               @RequestParam(name = "limit", required = false, defaultValue = "20") Integer limit) {
        model.addAttribute("bookList", bookService.getPageablePopularBooks(offset, limit));
        return "books/popular";
    }

    @GetMapping("/author/{slug}")
    public String booksByAuthor(Model model,
                                @PathVariable(name = "slug") String slug,
                                @RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset,
                                @RequestParam(value = "limit", required = false, defaultValue = "20") Integer limit) {
        model.addAttribute("author", authorService.getAuthorBySlug(slug));
        model.addAttribute("bookList", bookService.getPageableBooksByAuthorSlug(offset, limit, slug));
        return "books/author";
    }
}

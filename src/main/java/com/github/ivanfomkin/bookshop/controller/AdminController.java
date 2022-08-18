package com.github.ivanfomkin.bookshop.controller;

import com.github.ivanfomkin.bookshop.dto.book.BookEditDto;
import com.github.ivanfomkin.bookshop.service.Book2UserService;
import com.github.ivanfomkin.bookshop.service.BookService;
import com.github.ivanfomkin.bookshop.service.CookieService;
import com.github.ivanfomkin.bookshop.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@Controller
@RequestMapping("/admin")
public class AdminController extends ModelAttributeController {
    private final BookService bookService;

    public AdminController(UserService userService, CookieService cookieService, Book2UserService book2UserService, BookService bookService) {
        super(userService, cookieService, book2UserService);
        this.bookService = bookService;
    }

    @GetMapping
    public String getControlPanel() {
        return "admin/index";
    }

    @GetMapping("/books")
    public String getBookList(Model model,
                              @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                              @RequestParam(name = "perPage", required = false, defaultValue = "20") Integer perPage,
                              @RequestParam(name = "searchQuery", required = false, defaultValue = "") String searchQuery) {
        model.addAttribute("searchQuery", searchQuery);
        model.addAttribute("bookList", bookService.getPageableAllBooks(PageRequest.of(page, perPage, Sort.by(Sort.Direction.ASC, "title")), searchQuery));
        return "admin/book_list";
    }

    @GetMapping("/books/edit/{slug}")
    public String editBookPage(Model model, @PathVariable(name = "slug") String slug) {
        model.addAttribute("book", bookService.getBookEditDtoBySlug(slug));
        return "admin/book_edit";
    }

    @PutMapping("/books/edit/{slug}")
    public String editBook(@PathVariable(name = "slug") String slug, BookEditDto book) throws IOException {
        bookService.updateBookEntity(book);
        return "redirect:/admin/books/edit/" + book.getSlug();
    }
}

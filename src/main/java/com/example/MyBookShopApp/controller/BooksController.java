package com.example.MyBookShopApp.controller;

import com.example.MyBookShopApp.data.ResourceStorage;
import com.example.MyBookShopApp.dto.search.SearchDto;
import com.example.MyBookShopApp.service.AuthorService;
import com.example.MyBookShopApp.service.Book2UserService;
import com.example.MyBookShopApp.service.BookService;
import com.example.MyBookShopApp.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.file.Path;

@Slf4j
@Controller
@RequestMapping("/books")
public class BooksController {
    private final BookService bookService;
    private final UserService userService;
    private final AuthorService authorService;
    private final ResourceStorage resourceStorage;
    private final Book2UserService book2UserService;

    public BooksController(BookService bookService, AuthorService authorService, ResourceStorage resourceStorage, UserService userService, Book2UserService book2UserService) {
        this.userService = userService;
        this.bookService = bookService;
        this.authorService = authorService;
        this.resourceStorage = resourceStorage;
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

    @GetMapping("/{slug}")
    public String bookBySlug(Model model, @PathVariable String slug, HttpSession httpSession) {
        model.addAttribute("book", bookService.getBookSlugDtoBySlug(slug));
        if (userService.isAuthorized(httpSession)) {
            return "books/slugmy";
        } else {
            return "books/slug";
        }
    }

    @PostMapping("/{slug}/img/save")
    public String saveNewBookImage(@PathVariable String slug, @RequestParam("file") MultipartFile bookImage) throws IOException {
        String newBookImage = resourceStorage.saveNewBookImage(bookImage, slug);
        bookService.updateBookImageBySlug(slug, newBookImage);
        return "redirect:/books/" + slug;
    }

    @GetMapping("/download/{hash}")
    public ResponseEntity<ByteArrayResource> bookFileByHash(@PathVariable String hash) throws IOException {
        Path path = resourceStorage.getBookPathByHash(hash);
        byte[] data = resourceStorage.getBookFileByteArray(hash);
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + path.getFileName().toString())
                .contentType(resourceStorage.getBookFileMime(hash))
                .contentLength(data.length)
                .body(new ByteArrayResource(data));
    }
}

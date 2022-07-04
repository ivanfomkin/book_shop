package com.github.ivanfomkin.bookshop.controller;

import com.github.ivanfomkin.bookshop.data.ResourceStorage;
import com.github.ivanfomkin.bookshop.entity.user.UserEntity;
import com.github.ivanfomkin.bookshop.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

@Slf4j
@Controller
@RequestMapping("/books")
public class BooksController extends ModelAttributeController {
    private final BookService bookService;
    private final UserService userService;
    private final AuthorService authorService;
    private final ResourceStorage resourceStorage;

    public BooksController(UserService userService, Book2UserService book2UserService, CookieService cookieService, BookService bookService, UserService userService1, AuthorService authorService, ResourceStorage resourceStorage) {
        super(userService, cookieService, book2UserService);
        this.bookService = bookService;
        this.userService = userService1;
        this.authorService = authorService;
        this.resourceStorage = resourceStorage;
    }

    @GetMapping("/recent")
    public String recentBooks(Model model,
                              @RequestParam(name = "offset", required = false, defaultValue = "0") Integer offset,
                              @RequestParam(name = "limit", required = false, defaultValue = "20") Integer limit,
                              @RequestParam(name = "from", required = false, defaultValue = "0") String fromDate,
                              @RequestParam(name = "to", required = false, defaultValue = "0") String toDate,
                              @CookieValue(value = "cartContent", required = false, defaultValue = "") String cartCookie,
                              @CookieValue(value = "keptContent", required = false, defaultValue = "") String keptCookie) {
        model.addAttribute("bookList", bookService.getPageableRecentBooks(offset, limit, fromDate, toDate, cartCookie, keptCookie));
        return "books/recent";
    }

    @GetMapping("/popular")
    public String popularBooks(Model model,
                               @RequestParam(name = "offset", required = false, defaultValue = "0") Integer offset,
                               @RequestParam(name = "limit", required = false, defaultValue = "20") Integer limit,
                               @CookieValue(value = "cartContent", required = false, defaultValue = "") String cartCookie,
                               @CookieValue(value = "keptContent", required = false, defaultValue = "") String keptCookie) {
        model.addAttribute("bookList", bookService.getPageablePopularBooks(offset, limit, cartCookie, keptCookie));
        return "books/popular";
    }

    @GetMapping("/author/{slug}")
    public String booksByAuthor(Model model,
                                @PathVariable(name = "slug") String slug,
                                @RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset,
                                @RequestParam(value = "limit", required = false, defaultValue = "20") Integer limit,
                                @CookieValue(value = "cartContent", required = false, defaultValue = "") String cartCookie,
                                @CookieValue(value = "keptContent", required = false, defaultValue = "") String keptCookie) {
        model.addAttribute("author", authorService.getAuthorBySlug(slug));
        model.addAttribute("bookList", bookService.getPageableBooksByAuthorSlug(offset, limit, slug, cartCookie, keptCookie));
        return "books/author";
    }

    @GetMapping("/{slug}")
    public String bookBySlug(Model model,
                             @PathVariable String slug,
                             @CookieValue(value = "cartContent", required = false) String cartCookie,
                             @CookieValue(value = "keptContent", required = false) String keptCookie) {
        UserEntity currentUser = userService.getCurrentUser();
        model.addAttribute("book", bookService.getBookSlugDtoBySlug(currentUser, slug, cartCookie, keptCookie));
        if (currentUser != null) {
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

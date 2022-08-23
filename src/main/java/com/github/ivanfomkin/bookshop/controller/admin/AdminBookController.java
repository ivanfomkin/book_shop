package com.github.ivanfomkin.bookshop.controller.admin;

import com.github.ivanfomkin.bookshop.controller.ModelAttributeController;
import com.github.ivanfomkin.bookshop.dto.author.AuthorElementDto;
import com.github.ivanfomkin.bookshop.dto.book.BookEditDto;
import com.github.ivanfomkin.bookshop.dto.genre.GenreElementDto;
import com.github.ivanfomkin.bookshop.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/admin/books")
public class AdminBookController extends ModelAttributeController {
    private final TagService tagService;
    private final BookService bookService;
    private final GenreService genreService;
    private final AuthorService authorService;

    public AdminBookController(UserService userService, CookieService cookieService, Book2UserService book2UserService, TagService tagService, BookService bookService, GenreService genreService, AuthorService authorService) {
        super(userService, cookieService, book2UserService);
        this.tagService = tagService;
        this.bookService = bookService;
        this.genreService = genreService;
        this.authorService = authorService;
    }

    @GetMapping
    public String getBookList(Model model,
                              @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                              @RequestParam(name = "perPage", required = false, defaultValue = "20") Integer perPage,
                              @RequestParam(name = "searchQuery", required = false, defaultValue = "") String searchQuery) {
        model.addAttribute("searchQuery", searchQuery);
        model.addAttribute("bookList", bookService.getPageableAllBooks(PageRequest.of(page, perPage, Sort.by(Sort.Direction.ASC, "title")), searchQuery));
        return "admin/book_list";
    }

    @ModelAttribute("authors")
    public List<AuthorElementDto> authorElementDtoList() {
        return authorService.getAllAuthors();
    }

    @ModelAttribute("tagsList")
    public List<String> tagList() {
        return tagService.getAllTags();
    }

    @ModelAttribute("genreList")
    public List<GenreElementDto> genresList() {
        return genreService.getAllGenresDto();
    }

    @GetMapping("/edit/{slug}")
    public String editBookPage(Model model, @PathVariable(name = "slug") String slug) {
        model.addAttribute("book", bookService.getBookEditDtoBySlug(slug));
        model.addAttribute("method", "PUT");
        return "admin/book_edit";
    }

    @PutMapping("/edit/{slug}")
    public String editBook(@PathVariable(name = "slug") String slug, BookEditDto book) throws IOException {
        bookService.updateBookEntity(book);
        return "redirect:/admin/books/edit/" + book.getSlug();
    }

    @GetMapping("/create")
    public String createBookPage(Model model) {
        model.addAttribute("book", new BookEditDto());
        model.addAttribute("method", "POST");
        return "admin/book_edit";
    }

    @PostMapping("/create")
    public String createBook(BookEditDto bookEditDto) throws IOException {
        bookService.createBook(bookEditDto);
        return "redirect:/admin/books/";
    }

    @GetMapping("/delete/{slug}")
    public String deleteBookPage(Model model, @PathVariable(name = "slug") String slug) {
        model.addAttribute("method", "DELETE");
        return "admin/book_delete";
    }

    @DeleteMapping("/delete/{slug}")
    public String deleteAuthor(@PathVariable(name = "slug") String slug) {
        bookService.deleteBookBySlug(slug);
        return "redirect:/admin/books/";
    }
}

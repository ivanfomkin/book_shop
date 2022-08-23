package com.github.ivanfomkin.bookshop.controller.admin;

import com.github.ivanfomkin.bookshop.controller.ModelAttributeController;
import com.github.ivanfomkin.bookshop.dto.author.AuthorEditDto;
import com.github.ivanfomkin.bookshop.service.AuthorService;
import com.github.ivanfomkin.bookshop.service.Book2UserService;
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
@RequestMapping("/admin/authors")
public class AdminAuthorController extends ModelAttributeController {
    private final AuthorService authorService;

    public AdminAuthorController(UserService userService, CookieService cookieService, Book2UserService book2UserService, AuthorService authorService) {
        super(userService, cookieService, book2UserService);
        this.authorService = authorService;
    }

    @GetMapping
    public String getAuthorList(Model model,
                                @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                @RequestParam(name = "perPage", required = false, defaultValue = "20") Integer perPage,
                                @RequestParam(name = "searchQuery", required = false, defaultValue = "") String searchQuery) {
        model.addAttribute("searchQuery", searchQuery);
        model.addAttribute("authorList", authorService.getPageableAllAuthors(PageRequest.of(page, perPage, Sort.by(Sort.Direction.ASC, "name")), searchQuery));
        return "admin/author_list";
    }

    @GetMapping("/edit/{slug}")
    public String editAuthorPage(Model model, @PathVariable(name = "slug") String slug) {
        model.addAttribute("author", authorService.getAuthorEditDtoBySlug(slug));
        model.addAttribute("method", "PUT");
        return "admin/author_edit";
    }

    @PutMapping("/edit/{slug}")
    public String editAuthor(@PathVariable(name = "slug") String slug, AuthorEditDto editDto) throws IOException {
        authorService.updateAuthorEntity(editDto);
        return "redirect:/admin/authors/edit/" + editDto.getSlug();
    }

    @GetMapping("/create")
    public String createAuthorPage(Model model) {
        model.addAttribute("author", new AuthorEditDto());
        model.addAttribute("method", "POST");
        return "admin/author_edit";
    }

    @PostMapping("/create")
    public String editAuthor(AuthorEditDto editDto) throws IOException {
        authorService.createAuthorEntity(editDto);
        return "redirect:/admin/authors/edit/" + editDto.getSlug();
    }

    @GetMapping("/delete/{slug}")
    public String deleteAuthorPage(Model model, @PathVariable(name = "slug") String slug) {
        model.addAttribute("method", "DELETE");
        return "admin/author_delete";
    }

    @DeleteMapping("/delete/{slug}")
    public String deleteAuthor(@PathVariable(name = "slug") String slug) {
        authorService.deleteAuthorBySlug(slug);
        return "redirect:/admin/authors/";
    }
}

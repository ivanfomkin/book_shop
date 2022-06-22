package com.github.ivanfomkin.bookshop.controller;

import com.github.ivanfomkin.bookshop.entity.tag.TagWithWeightObject;
import com.github.ivanfomkin.bookshop.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@Controller
public class MainPageController extends ModelAttributeController {
    private final TagService tagService;
    private final BookService bookService;

    public MainPageController(UserService userService, CookieService cookieService, Book2UserService book2UserService, TagService tagService, BookService bookService) {
        super(userService, cookieService, book2UserService);
        this.tagService = tagService;
        this.bookService = bookService;
    }

    @ModelAttribute("tags")
    public List<TagWithWeightObject> tagCloud() {
        return tagService.getTagsWithWeight();
    }


    @GetMapping
    public String mainPage(Model model) {
        model.addAttribute("recentBooks", bookService.getPageableRecentBooks(0, 20));
        model.addAttribute("popularBooks", bookService.getPageablePopularBooks(0, 20));
        model.addAttribute("recommendedBooks", bookService.getPageableRecommendedBooks(0, 20));
        return "index";
    }
}

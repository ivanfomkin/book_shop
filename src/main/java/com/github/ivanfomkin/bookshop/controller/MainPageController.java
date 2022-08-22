package com.github.ivanfomkin.bookshop.controller;

import com.github.ivanfomkin.bookshop.entity.tag.TagWithWeightObject;
import com.github.ivanfomkin.bookshop.service.*;
import org.apache.mahout.cf.taste.common.TasteException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@Controller
public class MainPageController extends ModelAttributeController {
    private final TagService tagService;
    private final BookService bookService;

    private final RecommendationService recommendationService;
    public MainPageController(UserService userService, CookieService cookieService, Book2UserService book2UserService, TagService tagService, BookService bookService, RecommendationService recommendationService) {
        super(userService, cookieService, book2UserService);
        this.tagService = tagService;
        this.bookService = bookService;
        this.recommendationService = recommendationService;
    }

    @ModelAttribute("tags")
    public List<TagWithWeightObject> tagCloud() {
        return tagService.getTagsWithWeight();
    }


    @GetMapping
    public String mainPage(Model model,
                           @CookieValue(value = "cartContent", required = false, defaultValue = "") String cartCookie,
                           @CookieValue(value = "keptContent", required = false, defaultValue = "") String keptCookie) throws TasteException {
        model.addAttribute("recentBooks", bookService.getPageableRecentBooks(cartCookie, keptCookie, 0, 20));
        model.addAttribute("popularBooks", bookService.getPageablePopularBooks(0, 20, cartCookie, keptCookie));
        model.addAttribute("recommendedBooks", bookService.getPageableRecommendedBooks(0, 20, cartCookie, keptCookie));
        recommendationService.getRecommendedBooks();
        return "index";
    }
}

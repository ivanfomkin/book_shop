package com.example.MyBookShopApp.controller;

import com.example.MyBookShopApp.dto.search.SearchDto;
import com.example.MyBookShopApp.entity.tag.TagWithWeightObject;
import com.example.MyBookShopApp.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class MainPageController {

    private final BookService bookService;
    private final TagService tagService;
    private final UserService userService;
    private final CartService cartService;
    private final KeptService keptService;

    public MainPageController(BookService bookService, TagService tagService, UserService userService, CartService cartService, KeptService keptService) {
        this.bookService = bookService;
        this.tagService = tagService;
        this.userService = userService;
        this.cartService = cartService;
        this.keptService = keptService;
    }

    @ModelAttribute("searchDto")
    public SearchDto searchWord() {
        return new SearchDto();
    }

    @ModelAttribute("tags")
    public List<TagWithWeightObject> tagCloud() {
        return tagService.getTagsWithWeight();
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
    public String mainPage(Model model, HttpSession session) {
        model.addAttribute("recentBooks", bookService.getPageableRecentBooks(0, 20));
        model.addAttribute("popularBooks", bookService.getPageablePopularBooks(0, 20));
        model.addAttribute("recommendedBooks", bookService.getPageableRecommendedBooks(0, 20));
        return "index";
    }
}

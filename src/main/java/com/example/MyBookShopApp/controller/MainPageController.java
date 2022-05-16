package com.example.MyBookShopApp.controller;

import com.example.MyBookShopApp.dto.search.SearchDto;
import com.example.MyBookShopApp.entity.tag.TagWithWeightObject;
import com.example.MyBookShopApp.service.Book2UserService;
import com.example.MyBookShopApp.service.BookService;
import com.example.MyBookShopApp.service.TagService;
import com.example.MyBookShopApp.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.List;

@Controller
public class MainPageController {

    private final TagService tagService;
    private final UserService userService;
    private final BookService bookService;
    private final Book2UserService book2UserService;

    public MainPageController(TagService tagService, UserService userService, BookService bookService, Book2UserService book2UserService) {
        this.tagService = tagService;
        this.userService = userService;
        this.bookService = bookService;
        this.book2UserService = book2UserService;
    }


    @ModelAttribute("searchDto")
    public SearchDto searchWord() {
        return new SearchDto();
    }

    @ModelAttribute("tags")
    public List<TagWithWeightObject> tagCloud() {
        return tagService.getTagsWithWeight();
    }

//    @ModelAttribute("cartAmount")
//    public int cartAmount(HttpSession httpSession) {
//        return book2UserService.getCartAmount(userService.getUserBySession(httpSession));
//    }
//
//    @ModelAttribute("keptAmount")
//    public int keptAmount(HttpSession httpSession) {
//        return book2UserService.getKeptAmount(userService.getUserBySession(httpSession));
//    }

    @GetMapping
    public String mainPage(Model model, Principal principal) {
        model.addAttribute("recentBooks", bookService.getPageableRecentBooks(0, 20));
        model.addAttribute("popularBooks", bookService.getPageablePopularBooks(0, 20));
        model.addAttribute("recommendedBooks", bookService.getPageableRecommendedBooks(0, 20));
        return "index";
    }
}

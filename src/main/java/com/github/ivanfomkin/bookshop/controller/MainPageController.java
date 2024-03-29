package com.github.ivanfomkin.bookshop.controller;

import com.github.ivanfomkin.bookshop.dto.contact.ContactRequestDto;
import com.github.ivanfomkin.bookshop.entity.tag.TagWithWeightObject;
import com.github.ivanfomkin.bookshop.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class MainPageController extends ModelAttributeController {
    private final TagService tagService;
    private final BookService bookService;
    private final ContactService contactService;

    public MainPageController(UserService userService, CookieService cookieService, Book2UserService book2UserService, TagService tagService, BookService bookService, ContactService contactService) {
        super(userService, cookieService, book2UserService);
        this.tagService = tagService;
        this.bookService = bookService;
        this.contactService = contactService;
    }

    @ModelAttribute("tags")
    public List<TagWithWeightObject> tagCloud() {
        return tagService.getTagsWithWeight();
    }


    @GetMapping
    public String mainPage(Model model,
                           @CookieValue(value = "cartContent", required = false, defaultValue = "") String cartCookie,
                           @CookieValue(value = "keptContent", required = false, defaultValue = "") String keptCookie) {
        model.addAttribute("recentBooks", bookService.getPageableRecentBooks(cartCookie, keptCookie, 0, 20));
        model.addAttribute("popularBooks", bookService.getPageablePopularBooks(0, 20, cartCookie, keptCookie));
        model.addAttribute("recommendedBooks", bookService.getPageableRecommendedBooks(0, 10, cartCookie, keptCookie));
        return "index";
    }

    @PostMapping("/contacts")
    public String contacts(ContactRequestDto contactRequestDto) {
        contactService.handleContactRequest(contactRequestDto);
        return "redirect:/contacts";
    }
}

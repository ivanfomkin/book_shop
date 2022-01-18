package com.example.MyBookShopApp.controller;

import com.example.MyBookShopApp.dto.search.SearchDto;
import com.example.MyBookShopApp.entity.user.UserEntity;
import com.example.MyBookShopApp.service.Book2UserService;
import com.example.MyBookShopApp.service.CartService;
import com.example.MyBookShopApp.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/postponed")
public class PostponedController {

    private final UserService userService;
    private final CartService cartService;
    private final Book2UserService book2UserService;

    public PostponedController(UserService userService, CartService cartService, Book2UserService book2UserService) {
        this.userService = userService;
        this.cartService = cartService;
        this.book2UserService = book2UserService;
    }

    @ModelAttribute("cartAmount")
    public int cartAmount(HttpSession httpSession) {
        return book2UserService.getCartAmount(userService.getUserBySession(httpSession));
    }

    @ModelAttribute("searchDto")
    public SearchDto searchWord() {
        return new SearchDto();
    }

    @GetMapping
    public String postponedPage(Model model, HttpSession httpSession) {
        UserEntity user = userService.getUserBySession(httpSession);
        int keptAmount = book2UserService.getKeptAmount(user);

        model.addAttribute("keptAmount", keptAmount);
        model.addAttribute("isPostponedEmpty", keptAmount == 0);
        model.addAttribute("books", cartService.getPostponedBooks(user));
        return "postponed";
    }
}

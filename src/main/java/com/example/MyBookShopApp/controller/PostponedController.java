package com.example.MyBookShopApp.controller;

import com.example.MyBookShopApp.dto.cart.CartBookElementDto;
import com.example.MyBookShopApp.entity.user.UserEntity;
import com.example.MyBookShopApp.service.Book2UserService;
import com.example.MyBookShopApp.service.CartService;
import com.example.MyBookShopApp.service.CookieService;
import com.example.MyBookShopApp.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/postponed")
public class PostponedController extends ModelAttributeController {

    private final UserService userService;
    private final CartService cartService;

    public PostponedController(UserService userService, CookieService cookieService, Book2UserService book2UserService, CartService cartService) {
        super(userService, cookieService, book2UserService);
        this.userService = userService;
        this.cartService = cartService;
    }

    @GetMapping
    public String postponedPage(Model model, @ModelAttribute("keptAmount") Integer keptAmount, @CookieValue(value = "keptContent", required = false) String keptCookie) {
        UserEntity user = userService.getCurrentUser();
        List<CartBookElementDto> postponedBooks;
        if (user != null) {
            postponedBooks = cartService.getPostponedBooksByUser(user);
        } else {
            postponedBooks = cartService.getPostponedBooksFromCookie(keptCookie);
        }
        model.addAttribute("keptAmount", keptAmount);
        model.addAttribute("isPostponedEmpty", keptAmount == null || keptAmount == 0);
        model.addAttribute("books", postponedBooks);
        return "postponed";
    }
}

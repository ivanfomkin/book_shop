package com.example.MyBookShopApp.controller;

import com.example.MyBookShopApp.dto.cart.CartDto;
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
@RequestMapping("/cart")
public class CartController extends ModelAttributeController{
    private final CartService cartService;
    private final UserService userService;

    public CartController(CartService cartService, CookieService cookieService, UserService userService, Book2UserService book2UserService) {
        super(userService, cookieService, book2UserService);
        this.cartService = cartService;
        this.userService = userService;
    }

    @ModelAttribute("bookCart")
    public CartDto cart() {
        return new CartDto(List.of(), 0, 0);
    }

    @GetMapping
    public String cartPage(Model model, @CookieValue(value = "cartContent", required = false) String cartCookie) {
        var user = userService.getCurrentUser();
        CartDto cartDto;
        if (user == null) {
            cartDto = cartService.getCartDtoFromCookie(cartCookie);
        } else {
            cartDto = cartService.getCartDtoByUser(user);
        }
        model.addAttribute("cartAmount", cartDto.books().size());
        model.addAttribute("isCartEmpty", cartDto.books().size() == 0);
        model.addAttribute("bookCart", cartDto);
        return "cart";
    }
}

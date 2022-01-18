package com.example.MyBookShopApp.controller;

import com.example.MyBookShopApp.dto.cart.CartDto;
import com.example.MyBookShopApp.dto.search.SearchDto;
import com.example.MyBookShopApp.entity.enums.Book2UserType;
import com.example.MyBookShopApp.service.Book2UserService;
import com.example.MyBookShopApp.service.CartService;
import com.example.MyBookShopApp.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {
    private final CartService cartService;
    private final UserService userService;
    private final Book2UserService book2UserService;

    public CartController(CartService cartService, UserService userService, Book2UserService book2UserService) {
        this.cartService = cartService;
        this.userService = userService;
        this.book2UserService = book2UserService;
    }

    @ModelAttribute("searchDto")
    public SearchDto searchWord() {
        return new SearchDto();
    }

    @ModelAttribute("bookCart")
    public CartDto cart() {
        return new CartDto(List.of(), 0, 0);
    }

    @ModelAttribute("keptAmount")
    public int keptAmount(HttpSession httpSession) {
        return book2UserService.getKeptAmount(userService.getUserBySession(httpSession));
    }

    @GetMapping
    public String cartPage(HttpSession session,
                           Model model) {
        var user = userService.getUserBySession(session);
        var cartDto = cartService.getCartDtoByUser(user);
        model.addAttribute("cartAmount", cartDto.books().size());
        model.addAttribute("isCartEmpty", cartDto.books().size() == 0);
        model.addAttribute("bookCart", cartDto);
        return "cart";
    }

    @PostMapping("/changeBookStatus/{slug}")
    @ResponseBody
    public String changeBookStatus(@PathVariable String slug,
                                   @RequestParam(name = "status") Book2UserType status,
                                   HttpSession session,
                                   Model model) {
        book2UserService.changeBookStatus(userService.getUserBySession(session), slug, status);
        return "redirect:/books/" + slug;
    }
}

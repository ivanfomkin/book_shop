package com.example.MyBookShopApp.controller;

import com.example.MyBookShopApp.dto.cart.CartDto;
import com.example.MyBookShopApp.dto.search.SearchDto;
import com.example.MyBookShopApp.entity.enums.Book2UserType;
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

    public CartController(CartService cartService, UserService userService) {
        this.cartService = cartService;
        this.userService = userService;
    }

    @ModelAttribute("searchDto")
    public SearchDto searchWord() {
        return new SearchDto();
    }

    @ModelAttribute("bookCart")
    public CartDto cart() {
        return new CartDto(List.of(), 0, 0);
    }

    @GetMapping
    public String cartPage(HttpSession session,
                           Model model) {
        var user = userService.getUserBySession(session);
        var cartDto = cartService.getCartDtoByUser(user);
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
        cartService.addBookToCart(userService.getUserBySession(session), status, slug);
        return "redirect:/books/" + slug;
    }

    @PostMapping("/changeBookStatus/cart/remove/{slug}")
    public String removeBookFromCart(@PathVariable String slug,
                                     HttpSession session,
                                     Model model) {
        cartService.deleteBookFromCart(userService.getUserBySession(session), slug);
        return "redirect:/books/" + slug;
    }
}

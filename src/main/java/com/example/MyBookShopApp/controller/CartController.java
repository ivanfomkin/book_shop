package com.example.MyBookShopApp.controller;

import com.example.MyBookShopApp.dto.CartDto;
import com.example.MyBookShopApp.dto.search.SearchDto;
import com.example.MyBookShopApp.service.CartService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

@Controller
@RequestMapping("/cart")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
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
    public String cartPage(@CookieValue(name = "cartContents", required = false) String cartContents,
                           Model model) {
        if (cartContents == null || cartContents.isEmpty()) {
            model.addAttribute("isCartEmpty", true);
        } else {
            model.addAttribute("isCartEmpty", false);
            model.addAttribute("bookCart", cartService.getCartDtoByCookie(cartContents));
        }
        return "cart";
    }

    @PostMapping("/changeBookStatus/{slug}")
    @ResponseBody
    public String changeBookStatus(@PathVariable String slug,
                                   @CookieValue(name = "cartContents", required = false) String cartContents,
                                   HttpServletResponse response,
                                   Model model) {
        if (cartContents == null || cartContents.isEmpty()) {
            Cookie cookie = new Cookie("cartContents", slug);
            cookie.setPath("/");
            response.addCookie(cookie);
            model.addAttribute("isCartEmpty", false);
        } else if (!cartContents.contains(slug)) {
            StringJoiner stringJoiner = new StringJoiner("/");
            stringJoiner.add(cartContents).add(slug);
            Cookie cookie = new Cookie("cartContents", stringJoiner.toString());
            cookie.setPath("/");
            response.addCookie(cookie);
            model.addAttribute("isCartEmpty", false);
        }
        return "redirect:/books/" + slug;
    }

    @PostMapping("/changeBookStatus/cart/remove/{slug}")
    public String removeBookFromCart(@PathVariable String slug,
                                     @CookieValue(name = "cartContents", required = false) String cartContents,
                                     HttpServletResponse response,
                                     Model model) {
        if (cartContents != null || !cartContents.isEmpty()) {
            var bookSlugs = new ArrayList<>(Arrays.asList(cartContents.split("/")));
            bookSlugs.remove(slug);
            Cookie cookie = new Cookie("cartContents", String.join("/", bookSlugs));
            cookie.setPath("/");
            response.addCookie(cookie);
            model.addAttribute("isCartEmpty", bookSlugs.size() > 0);
        } else {
            model.addAttribute("isCartEmpty", true);
        }
        return "redirect:/books/" + slug;
    }
}

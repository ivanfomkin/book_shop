package com.example.MyBookShopApp.controller;

import com.example.MyBookShopApp.dto.search.SearchDto;
import com.example.MyBookShopApp.dto.user.UserDto;
import com.example.MyBookShopApp.entity.user.UserEntity;
import com.example.MyBookShopApp.service.Book2UserService;
import com.example.MyBookShopApp.service.CookieService;
import com.example.MyBookShopApp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;

@RequiredArgsConstructor
public class ModelAttributeController {
    private final UserService userService;
    private final CookieService cookieService;
    private final Book2UserService book2UserService;

    @ModelAttribute("cartAmount")
    public int cartAmount(@CookieValue(value = "cartContent", required = false) String cookieCart) {
        UserEntity currentUser = userService.getCurrentUser();
        return currentUser == null ? cookieService.getCookieSize(cookieCart) : book2UserService.getCartAmount(currentUser);
    }

    @ModelAttribute("keptAmount")
    public int keptAmount(@CookieValue(value = "keptContent", required = false) String cookieKept) {
        UserEntity currentUser = userService.getCurrentUser();
        return currentUser == null ? cookieService.getCookieSize(cookieKept) : book2UserService.getKeptAmount(currentUser);
    }

    @ModelAttribute("userStatus")
    public String userStatus() {
        return userService.getCurrentUser() == null ? "unauthorized" : "authorized";
    }

    @ModelAttribute("searchDto")
    public SearchDto searchWord() {
        return new SearchDto();
    }

    @ModelAttribute("user")
    public UserDto currentUser() {
        return userService.getCurrentUserInfo();
    }

}


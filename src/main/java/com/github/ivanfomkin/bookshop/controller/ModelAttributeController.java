package com.github.ivanfomkin.bookshop.controller;

import com.github.ivanfomkin.bookshop.dto.search.SearchDto;
import com.github.ivanfomkin.bookshop.dto.user.UserDto;
import com.github.ivanfomkin.bookshop.entity.user.UserEntity;
import com.github.ivanfomkin.bookshop.service.Book2UserService;
import com.github.ivanfomkin.bookshop.service.CookieService;
import com.github.ivanfomkin.bookshop.service.UserService;
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

    @ModelAttribute("myBookAmount")
    public int myBookAmount() {
        UserEntity currentUser = userService.getCurrentUser();
        return currentUser == null ? 0 : book2UserService.getMyBookAmount(currentUser);
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


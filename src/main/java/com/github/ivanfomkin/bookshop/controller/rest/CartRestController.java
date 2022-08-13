package com.github.ivanfomkin.bookshop.controller.rest;

import com.github.ivanfomkin.bookshop.dto.CommonResultDto;
import com.github.ivanfomkin.bookshop.dto.cart.ChangeBookStatusRequestDto;
import com.github.ivanfomkin.bookshop.entity.user.UserEntity;
import com.github.ivanfomkin.bookshop.service.Book2UserService;
import com.github.ivanfomkin.bookshop.service.CookieService;
import com.github.ivanfomkin.bookshop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/cart/")
@RequiredArgsConstructor
public class CartRestController {
    private final UserService userService;
    private final CookieService cookieService;
    private final Book2UserService book2UserService;

    @PostMapping("/changeBookStatus")
    public CommonResultDto changeBookStatus(@RequestBody ChangeBookStatusRequestDto dto, HttpServletRequest request, HttpServletResponse response) {
        UserEntity currentUser = userService.getCurrentUser();
        if (currentUser != null) {
            var slugs = dto.getSlugs().split(",");
            for (String slug : slugs) {
                book2UserService.changeBookStatus(currentUser, slug, dto.getStatus());
            }
        } else {
            cookieService.changeBookStatus(request.getCookies(), response, dto);
        }
        return new CommonResultDto(true);
    }
}

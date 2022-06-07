package com.example.MyBookShopApp.controller.rest;

import com.example.MyBookShopApp.dto.cart.ChangeBookStatusRequestDto;
import com.example.MyBookShopApp.entity.user.UserEntity;
import com.example.MyBookShopApp.service.Book2UserService;
import com.example.MyBookShopApp.service.CookieService;
import com.example.MyBookShopApp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequestMapping("/api/cart/")
@RequiredArgsConstructor
public class CartRestController {
    private final UserService userService;
    private final CookieService cookieService;
    private final Book2UserService book2UserService;

    @PostMapping("/changeBookStatus")
    public Map<String, Boolean> changeBookStatus(@RequestBody ChangeBookStatusRequestDto dto, HttpServletRequest request, HttpServletResponse response) {
        UserEntity currentUser = userService.getCurrentUser();
        if (currentUser != null) {
            book2UserService.changeBookStatus(currentUser, dto.getSlug(), dto.getStatus());
        } else {
            cookieService.changeBookStatus(request.getCookies(), response, dto);
        }
        return Map.of("result", true);
    }
}

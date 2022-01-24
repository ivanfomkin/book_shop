package com.example.MyBookShopApp.controller.rest;

import com.example.MyBookShopApp.dto.cart.ChangeBookStatusRequestDto;
import com.example.MyBookShopApp.service.Book2UserService;
import com.example.MyBookShopApp.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/api/cart/")
public class CartRestController {
    private final UserService userService;
    private final Book2UserService book2UserService;

    public CartRestController(UserService userService, Book2UserService book2UserService) {
        this.userService = userService;
        this.book2UserService = book2UserService;
    }

    @PostMapping("/changeBookStatus")
    public Map<String, Boolean> changeBookStatus(@RequestBody ChangeBookStatusRequestDto dto,
                                                 HttpSession session) {
        book2UserService.changeBookStatus(userService.getUserBySession(session), dto.getSlug(), dto.getStatus());
        return Map.of("result", true);
    }
}

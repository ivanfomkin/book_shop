package com.example.MyBookShopApp.controller.rest;

import com.example.MyBookShopApp.dto.CommonResultDto;
import com.example.MyBookShopApp.dto.security.ContactConfirmationRequestDto;
import com.example.MyBookShopApp.dto.security.ContactConfirmationResponse;
import com.example.MyBookShopApp.entity.user.UserEntity;
import com.example.MyBookShopApp.service.CartService;
import com.example.MyBookShopApp.service.UserService;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
public class SecurityRestController {
    private final UserService userService;
    private final CartService cartService;

    public SecurityRestController(UserService userService, CartService cartService) {
        this.userService = userService;
        this.cartService = cartService;
    }

    @PostMapping("/approveContact")
    public CommonResultDto approveContact(@RequestBody ContactConfirmationRequestDto dto) {
        return new CommonResultDto(true);
    }

    @PostMapping("/requestContactConfirmation")
    public CommonResultDto contactConfirmation(@RequestBody ContactConfirmationRequestDto dto) {
        return new CommonResultDto(true);
    }

    @PostMapping("/login")
    public ContactConfirmationResponse login(@RequestBody ContactConfirmationRequestDto dto,
                                             @CookieValue(value = "cartContent", required = false) String cartCookie,
                                             @CookieValue(value = "keptContent", required = false) String keptCookie,
                                             HttpServletResponse response) {
        ContactConfirmationResponse contactConfirmationResponse = userService.jwtLogin(dto);
        if (contactConfirmationResponse.getResult()) {
            Cookie cookie = new Cookie("token", contactConfirmationResponse.getToken());
            response.addCookie(cookie);
            UserEntity user = userService.getCurrentUser();
            cartService.mergeCartWithUser(cartCookie, user);
            cartService.mergeKeptWithUser(keptCookie, user);
        }
        return contactConfirmationResponse;
    }
}

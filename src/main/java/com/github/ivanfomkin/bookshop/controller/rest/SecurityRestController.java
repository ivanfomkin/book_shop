package com.github.ivanfomkin.bookshop.controller.rest;

import com.github.ivanfomkin.bookshop.dto.CommonResultDto;
import com.github.ivanfomkin.bookshop.dto.security.ContactConfirmationRequestDto;
import com.github.ivanfomkin.bookshop.dto.security.ContactConfirmationResponse;
import com.github.ivanfomkin.bookshop.entity.user.UserEntity;
import com.github.ivanfomkin.bookshop.service.CartService;
import com.github.ivanfomkin.bookshop.service.UserContactService;
import com.github.ivanfomkin.bookshop.service.UserService;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class SecurityRestController {
    private final UserService userService;
    private final CartService cartService;
    private final UserContactService userContactService;

    public SecurityRestController(UserService userService, CartService cartService, UserContactService userContactService) {
        this.userService = userService;
        this.cartService = cartService;
        this.userContactService = userContactService;
    }

    @PostMapping("/approveContact")
    public CommonResultDto approveContact(@RequestBody ContactConfirmationRequestDto dto) {
        return userContactService.approveContact(dto);
    }

    @PostMapping("/requestContactConfirmation")
    public CommonResultDto contactConfirmation(@RequestBody ContactConfirmationRequestDto dto, HttpServletRequest request) {
        return userContactService.sendContactConfirmationCode(dto, request.getRemoteAddr());
    }

    @PostMapping("/login")
    public ContactConfirmationResponse login(@RequestBody ContactConfirmationRequestDto dto,
                                             @CookieValue(value = "cartContent", required = false) String cartCookie,
                                             @CookieValue(value = "keptContent", required = false) String keptCookie,
                                             HttpServletResponse response) {
        ContactConfirmationResponse contactConfirmationResponse = userService.jwtLogin(dto);
        if (contactConfirmationResponse.isResult()) {
            Cookie cookie = new Cookie("token", contactConfirmationResponse.getToken());
            response.addCookie(cookie);
            UserEntity user = userService.getCurrentUser();
            cartService.mergeCartWithUser(cartCookie, user);
            cartService.mergeKeptWithUser(keptCookie, user);
        }
        return contactConfirmationResponse;
    }
}

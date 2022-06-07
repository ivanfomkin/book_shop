package com.example.MyBookShopApp.controller.rest;

import com.example.MyBookShopApp.dto.security.ContactConfirmationRequestDto;
import com.example.MyBookShopApp.dto.security.ContactConfirmationResponse;
import com.example.MyBookShopApp.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
public class SecurityRestController {
    private final UserService userService;

    public SecurityRestController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/approveContact")
    public Map<String, Object> approveContact(@RequestBody ContactConfirmationRequestDto dto) {
        return Map.of("result", true);
    }

    @PostMapping("/requestContactConfirmation")
    public Map<String, Object> contactConfirmation(@RequestBody ContactConfirmationRequestDto dto) {
        return Map.of("result", true);
    }

    @PostMapping("/login")
    public ContactConfirmationResponse login(@RequestBody ContactConfirmationRequestDto dto, HttpServletResponse response) {
        ContactConfirmationResponse contactConfirmationResponse = userService.jwtLogin(dto);
        if (contactConfirmationResponse.getResult()) {
            Cookie cookie = new Cookie("token", contactConfirmationResponse.getToken());
            response.addCookie(cookie);
        }
        return contactConfirmationResponse;
    }
}

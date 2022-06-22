package com.github.ivanfomkin.bookshop.controller;

import com.github.ivanfomkin.bookshop.dto.security.RegistrationFormDto;
import com.github.ivanfomkin.bookshop.service.Book2UserService;
import com.github.ivanfomkin.bookshop.service.CookieService;
import com.github.ivanfomkin.bookshop.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
@Controller
public class SecurityController extends ModelAttributeController {

    private final UserService userService;

    public SecurityController(UserService userService, CookieService cookieService, Book2UserService book2UserService, UserService userService1) {
        super(userService, cookieService, book2UserService);
        this.userService = userService1;
    }

    @GetMapping("/signin")
    public String signInPage() {
        return "signin";
    }

    @GetMapping("/signup")
    public String signUpPage(Model model) {
        model.addAttribute("regForm", new RegistrationFormDto());
        return "signup";
    }

    @PostMapping("/registration")
    public String registerUser(RegistrationFormDto formDto, Model model) {
        var newUser = userService.registerNewUser(formDto);
        model.addAttribute("registrationSuccess", newUser != null);
        return "signin";
    }

    @GetMapping("/my")
    public String myPage() {
        return "my";
    }

    @GetMapping("/myarchive")
    public String myArchive() {
        return "myarchive";
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        model.addAttribute("user", userService.getCurrentUser());
        return "profile";
    }
}

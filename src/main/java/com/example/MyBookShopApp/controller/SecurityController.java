package com.example.MyBookShopApp.controller;

import com.example.MyBookShopApp.dto.security.RegistrationFormDto;
import com.example.MyBookShopApp.service.Book2UserService;
import com.example.MyBookShopApp.service.CookieService;
import com.example.MyBookShopApp.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
@Controller
public class SecurityController extends ModelAttributeController{

    private final UserService userService;

    public SecurityController(UserService userService, CookieService cookieService, Book2UserService book2UserService, UserService userService1) {
        super(userService,cookieService, book2UserService);
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
        userService.registerNewUser(formDto);
        model.addAttribute("registrationSuccess", true);
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

package com.github.ivanfomkin.bookshop.controller;

import com.github.ivanfomkin.bookshop.dto.payment.PaymentRequestDto;
import com.github.ivanfomkin.bookshop.dto.security.RegistrationFormDto;
import com.github.ivanfomkin.bookshop.dto.user.UpdateProfileDto;
import com.github.ivanfomkin.bookshop.service.Book2UserService;
import com.github.ivanfomkin.bookshop.service.BookService;
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
    private final BookService bookService;

    public SecurityController(UserService userService, CookieService cookieService, Book2UserService book2UserService, UserService userService1, BookService bookService) {
        super(userService, cookieService, book2UserService);
        this.userService = userService1;
        this.bookService = bookService;
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
    public String myPage(Model model) {
        model.addAttribute("bookList", bookService.getPaidBooksByCurrentUser());
        return "my";
    }

    @GetMapping("/myarchive")
    public String myArchive(Model model) {
        model.addAttribute("bookList", bookService.getArchivedBooksByCurrentUser());
        return "myarchive";
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        model.addAttribute("userPage", userService.getUserPageDto());
        model.addAttribute("payment", new PaymentRequestDto());
        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile(Model model, UpdateProfileDto updateProfileDto) {
        userService.updateProfile(updateProfileDto);
        return "redirect:/profile";
    }
}

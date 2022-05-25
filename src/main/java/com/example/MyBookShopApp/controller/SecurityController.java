package com.example.MyBookShopApp.controller;

import com.example.MyBookShopApp.dto.search.SearchDto;
import com.example.MyBookShopApp.dto.security.RegistrationFormDto;
import com.example.MyBookShopApp.service.Book2UserService;
import com.example.MyBookShopApp.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;
import java.security.Principal;

@Slf4j
@Controller
public class SecurityController {

    private final UserService userService;
    private final Book2UserService book2UserService;

    public SecurityController(UserService userService, Book2UserService book2UserService) {
        this.userService = userService;
        this.book2UserService = book2UserService;
    }

    @ModelAttribute("searchDto")
    public SearchDto searchWord() {
        return new SearchDto();
    }

    @ModelAttribute("cartAmount")
    public int cartAmount(HttpSession httpSession) {
        return book2UserService.getCartAmount(userService.getUserBySession(httpSession));
    }

    @ModelAttribute("keptAmount")
    public int keptAmount(HttpSession httpSession) {
        return book2UserService.getKeptAmount(userService.getUserBySession(httpSession));
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
    public String registerUser(RegistrationFormDto formDto, HttpSession httpSession, Model model) {
        userService.registerNewUser(formDto, httpSession);
        model.addAttribute("registrationSuccess", true);
        return "signin";
    }

    @GetMapping("/my")
    public String myPage(Principal principal) {
        return "my";
    }

    @GetMapping("/myarchive")
    public String myArchive() {
        return "myarchive";
    }

    @GetMapping("/profile")
    public String profile(Model model, Principal principal) {
        model.addAttribute("user", userService.getCurrentUser());
        return "profile";
    }

//    @GetMapping("/logout")
//    public String logout(HttpSession session) {
//        SecurityContextHolder.clearContext();
//        if (session != null) {
//            session.invalidate();
//        }
//        return "redirect:/";
//    }
}

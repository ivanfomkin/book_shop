package com.github.ivanfomkin.bookshop.controller;

import com.github.ivanfomkin.bookshop.dto.payment.PaymentRequestDto;
import com.github.ivanfomkin.bookshop.dto.security.RegistrationFormDto;
import com.github.ivanfomkin.bookshop.dto.user.UpdateProfileDto;
import com.github.ivanfomkin.bookshop.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
public class SecurityController extends ModelAttributeController {

    private final UserService userService;
    private final BookService bookService;
    private final TransactionService transactionService;

    public SecurityController(UserService userService, CookieService cookieService, Book2UserService book2UserService, UserService userService1, BookService bookService, TransactionService transactionService) {
        super(userService, cookieService, book2UserService);
        this.userService = userService1;
        this.bookService = bookService;
        this.transactionService = transactionService;
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
        model.addAttribute("transactions", transactionService.getTransactionHistoryByUser(userService.getCurrentUser()));
        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile(RedirectAttributes redirectAttributes, UpdateProfileDto updateProfileDto) {
        userService.updateProfile(updateProfileDto);
        redirectAttributes.addFlashAttribute("updateSuccess", true);
        return "redirect:/profile";
    }

    @GetMapping("/profile/change/{token}")
    public String updateProfileConfirm(@PathVariable("token") String token, RedirectAttributes redirectAttributes) {
        userService.updateProfileConfirm(token);
        redirectAttributes.addFlashAttribute("updateConfirmSuccess", true);
        return "redirect:/profile";
    }
}

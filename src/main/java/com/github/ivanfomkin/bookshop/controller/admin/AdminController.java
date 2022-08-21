package com.github.ivanfomkin.bookshop.controller.admin;

import com.github.ivanfomkin.bookshop.controller.ModelAttributeController;
import com.github.ivanfomkin.bookshop.service.Book2UserService;
import com.github.ivanfomkin.bookshop.service.CookieService;
import com.github.ivanfomkin.bookshop.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController extends ModelAttributeController {
    public AdminController(UserService userService, CookieService cookieService, Book2UserService book2UserService) {
        super(userService, cookieService, book2UserService);
    }

    @GetMapping
    public String getControlPanel() {
        return "admin/index";
    }
}

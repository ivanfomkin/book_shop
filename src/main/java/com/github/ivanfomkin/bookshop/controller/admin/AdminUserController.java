package com.github.ivanfomkin.bookshop.controller.admin;

import com.github.ivanfomkin.bookshop.controller.ModelAttributeController;
import com.github.ivanfomkin.bookshop.service.Book2UserService;
import com.github.ivanfomkin.bookshop.service.CookieService;
import com.github.ivanfomkin.bookshop.service.UserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController extends ModelAttributeController {
    private final UserService userService;

    public AdminUserController(UserService userService, CookieService cookieService, Book2UserService book2UserService) {
        super(userService, cookieService, book2UserService);
        this.userService = userService;
    }

    @GetMapping
    public String userListPage(Model model,
                               @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                               @RequestParam(name = "perPage", required = false, defaultValue = "20") Integer perPage,
                               @RequestParam(name = "searchQuery", required = false, defaultValue = "") String searchQuery) {
        model.addAttribute("searchQuery", searchQuery);
        model.addAttribute("users", userService.getPageableAllUsers(PageRequest.of(page, perPage, Sort.by(Sort.Direction.ASC, "name")), searchQuery));
        return "admin/user_list";
    }
}

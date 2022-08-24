package com.github.ivanfomkin.bookshop.controller.admin;

import com.github.ivanfomkin.bookshop.controller.ModelAttributeController;
import com.github.ivanfomkin.bookshop.dto.user.GiftDto;
import com.github.ivanfomkin.bookshop.entity.enums.Book2UserType;
import com.github.ivanfomkin.bookshop.service.Book2UserService;
import com.github.ivanfomkin.bookshop.service.BookService;
import com.github.ivanfomkin.bookshop.service.CookieService;
import com.github.ivanfomkin.bookshop.service.UserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController extends ModelAttributeController {
    private final UserService userService;
    private final Book2UserService book2UserService;

    private final BookService bookService;

    public AdminUserController(UserService userService, CookieService cookieService, Book2UserService book2UserService, Book2UserService book2UserService1, BookService bookService) {
        super(userService, cookieService, book2UserService);
        this.userService = userService;
        this.book2UserService = book2UserService1;
        this.bookService = bookService;
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

    @GetMapping("/edit/shelf/{userId}")
    public String editUserBookShelfPage(Model model, @PathVariable Integer userId) {
        model.addAttribute("bookShelf", book2UserService.getUserBookShelf(userId));
        model.addAttribute("id", userId);
        return "admin/user_bookshelf_edit";
    }

    @GetMapping("/edit/shelf/{userId}/gift")
    public String giftBookToUserPage(Model model, @PathVariable Integer userId) {
        model.addAttribute("books", bookService.getBooksForUserGift(userId));
        model.addAttribute("gift", new GiftDto());
        return "admin/gift_books";
    }

    @PostMapping("/edit/shelf/{userId}/gift")
    public String giftBookToUser(@PathVariable Integer userId, GiftDto dto) {
        book2UserService.changeBookStatus(userId, dto.getBookSlugs(), Book2UserType.PAID);
        return "redirect:/admin/users/edit/shelf/" + userId;
    }

    @DeleteMapping("/delete/shelf/{bookToUserId}")
    public String deleteBookShelfElement(@PathVariable Integer bookToUserId) {
        var userId = book2UserService.deleteByBook2UserId(bookToUserId);
        return "redirect:/admin/users/edit/shelf/" + userId;
    }
}

package com.github.ivanfomkin.bookshop.controller.admin;

import com.github.ivanfomkin.bookshop.controller.ModelAttributeController;
import com.github.ivanfomkin.bookshop.service.Book2UserService;
import com.github.ivanfomkin.bookshop.service.BookReviewService;
import com.github.ivanfomkin.bookshop.service.CookieService;
import com.github.ivanfomkin.bookshop.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/admin/reviews")
public class AdminReviewController extends ModelAttributeController {
    private final BookReviewService bookReviewService;

    public AdminReviewController(UserService userService, CookieService cookieService, Book2UserService book2UserService, BookReviewService bookReviewService) {
        super(userService, cookieService, book2UserService);
        this.bookReviewService = bookReviewService;
    }

    @GetMapping
    public String listReviewPage(Model model,
                                 @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                 @RequestParam(name = "perPage", required = false, defaultValue = "20") Integer perPage,
                                 @RequestParam(name = "searchQuery", required = false, defaultValue = "") String searchQuery) {
        model.addAttribute("reviews", bookReviewService.getPageableAllReviews(PageRequest.of(page, perPage, Sort.by(Sort.Direction.DESC, "time")), searchQuery));
        return "admin/review_list";
    }

    @DeleteMapping("/delete/{reviewId}")
    public String deleteReview(@PathVariable Integer reviewId) {
        bookReviewService.deleteReviewById(reviewId);
        return "redirect:/admin/reviews";
    }
}

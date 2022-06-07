package com.example.MyBookShopApp.controller.rest;

import com.example.MyBookShopApp.dto.book.BookListDto;
import com.example.MyBookShopApp.dto.book.rate.BookRateRequestDto;
import com.example.MyBookShopApp.dto.book.review.BookReviewLikeRequestDto;
import com.example.MyBookShopApp.dto.book.review.BookReviewRequestDto;
import com.example.MyBookShopApp.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("api/books")
public class BookRestController {
    private final BookService bookService;
    private final UserService userService;
    private final GenreService genreService;
    private final BookVoteService bookVoteService;
    private final BookReviewService bookReviewService;
    private final BookReviewLikeService bookReviewLikeService;


    public BookRestController(BookService bookService, UserService userService, GenreService genreService, BookVoteService bookVoteService, Book2UserService book2UserService, BookReviewService bookReviewService, BookReviewLikeService bookReviewLikeService) {
        this.bookService = bookService;
        this.userService = userService;
        this.genreService = genreService;
        this.bookVoteService = bookVoteService;
        this.bookReviewService = bookReviewService;
        this.bookReviewLikeService = bookReviewLikeService;
    }

    @GetMapping("/recommended")
    public BookListDto recommendedBooks(@RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset,
                                        @RequestParam(value = "limit", required = false, defaultValue = "20") Integer limit) {
        return bookService.getPageableRecommendedBooks(offset, limit);
    }

    @GetMapping("/recent")
    public BookListDto recentBooks(@RequestParam(name = "offset", required = false, defaultValue = "0") Integer offset,
                                   @RequestParam(name = "limit", required = false, defaultValue = "20") Integer limit,
                                   @RequestParam(name = "from", required = false, defaultValue = "0") String fromDate,
                                   @RequestParam(name = "to", required = false, defaultValue = "0") String toDate) {
        return bookService.getPageableRecentBooks(offset, limit, fromDate, toDate);
    }

    @GetMapping("/popular")
    public BookListDto popularBooks(@RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset,
                                    @RequestParam(value = "limit", required = false, defaultValue = "20") Integer limit) {
        return bookService.getPageablePopularBooks(offset, limit);
    }

    @GetMapping("/author/{slug}")
    public BookListDto booksByAuthor(
            @PathVariable(name = "slug") String slug,
            @RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset,
            @RequestParam(value = "limit", required = false, defaultValue = "20") Integer limit) {
        return bookService.getPageableBooksByAuthorSlug(offset, limit, slug);
    }

    @GetMapping("/tag/{name}")
    public BookListDto booksByTag(
            @PathVariable(name = "name") String tag,
            @RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset,
            @RequestParam(value = "limit", required = false, defaultValue = "20") Integer limit) {
        return bookService.getPageableBooksByTag(offset, limit, tag);
    }

    @GetMapping("/genre/{slug}")
    public BookListDto booksByGenre(@RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset,
                                    @RequestParam(value = "limit", required = false, defaultValue = "20") Integer limit,
                                    @PathVariable String slug) {
        var genre = genreService.getGenreBySlug(slug);
        return bookService.getPageableBooksByGenre(offset, limit, genre);
    }

    @PostMapping("/rateBook")
    public Map<String, Boolean> rateBook(@RequestBody BookRateRequestDto bookRateRequestDto) {
        var result = true;
        try {
            bookVoteService.rateBook(userService.getCurrentUser(), bookService.getBookEntityBySlug(bookRateRequestDto.getBookId()), bookRateRequestDto.getValue());
        } catch (Exception e) {
            log.error("Book vote error: {}", e.getMessage());
            result = false;
        }
        return Map.of("result", result);
    }

    @PostMapping("/bookReview")
    public Map<String, Object> reviewBook(@RequestBody BookReviewRequestDto dto) {
        return bookReviewService.saveBookReview(
                bookService.getBookEntityBySlug(dto.getBookId()), dto.getText(), userService.getCurrentUser());
    }

    @PostMapping("/rateBookReview")
    public Map<String, Boolean> rateBookReview(@RequestBody BookReviewLikeRequestDto dto) {
        bookReviewLikeService.saveBookReviewLike(dto, userService.getCurrentUser());
        return Map.of("result", true);
    }
}

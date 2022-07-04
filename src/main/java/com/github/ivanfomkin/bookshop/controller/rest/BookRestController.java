package com.github.ivanfomkin.bookshop.controller.rest;

import com.github.ivanfomkin.bookshop.aop.annotation.ExecutionTimeLog;
import com.github.ivanfomkin.bookshop.dto.CommonResultDto;
import com.github.ivanfomkin.bookshop.dto.book.BookListDto;
import com.github.ivanfomkin.bookshop.dto.book.rate.BookRateRequestDto;
import com.github.ivanfomkin.bookshop.dto.book.review.BookReviewLikeRequestDto;
import com.github.ivanfomkin.bookshop.dto.book.review.BookReviewRequestDto;
import com.github.ivanfomkin.bookshop.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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


    public BookRestController(BookService bookService, UserService userService, GenreService genreService, BookVoteService bookVoteService, BookReviewService bookReviewService, BookReviewLikeService bookReviewLikeService) {
        this.bookService = bookService;
        this.userService = userService;
        this.genreService = genreService;
        this.bookVoteService = bookVoteService;
        this.bookReviewService = bookReviewService;
        this.bookReviewLikeService = bookReviewLikeService;
    }

    @GetMapping("/recommended")
    public BookListDto recommendedBooks(@RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset,
                                        @RequestParam(value = "limit", required = false, defaultValue = "20") Integer limit,
                                        @CookieValue(value = "cartContent", required = false, defaultValue = "") String cartCookie,
                                        @CookieValue(value = "keptContent", required = false, defaultValue = "") String keptCookie) {
        return bookService.getPageableRecommendedBooks(offset, limit, cartCookie, keptCookie);
    }

    @GetMapping("/recent")
    public BookListDto recentBooks(@RequestParam(name = "offset", required = false, defaultValue = "0") Integer offset,
                                   @RequestParam(name = "limit", required = false, defaultValue = "20") Integer limit,
                                   @RequestParam(name = "from", required = false, defaultValue = "0") String fromDate,
                                   @CookieValue(value = "cartContent", required = false, defaultValue = "") String cartCookie,
                                   @CookieValue(value = "keptContent", required = false, defaultValue = "") String keptCookie,
                                   @RequestParam(name = "to", required = false, defaultValue = "0") String toDate) {
        return bookService.getPageableRecentBooks(offset, limit, fromDate, toDate, cartCookie, keptCookie);
    }

    @GetMapping("/popular")
    public BookListDto popularBooks(@RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset,
                                    @RequestParam(value = "limit", required = false, defaultValue = "20") Integer limit,
                                    @CookieValue(value = "cartContent", required = false, defaultValue = "") String cartCookie,
                                    @CookieValue(value = "keptContent", required = false, defaultValue = "") String keptCookie) {
        return bookService.getPageablePopularBooks(offset, limit, cartCookie, keptCookie);
    }

    @GetMapping("/author/{slug}")
    public BookListDto booksByAuthor(
            @PathVariable(name = "slug") String slug,
            @RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset,
            @RequestParam(value = "limit", required = false, defaultValue = "20") Integer limit,
            @CookieValue(value = "cartContent", required = false, defaultValue = "") String cartCookie,
            @CookieValue(value = "keptContent", required = false, defaultValue = "") String keptCookie) {
        return bookService.getPageableBooksByAuthorSlug(offset, limit, slug, cartCookie, keptCookie);
    }

    @GetMapping("/tag/{name}")
    public BookListDto booksByTag(
            @PathVariable(name = "name") String tag,
            @RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset,
            @RequestParam(value = "limit", required = false, defaultValue = "20") Integer limit,
            @CookieValue(value = "cartContent", required = false, defaultValue = "") String cartCookie,
            @CookieValue(value = "keptContent", required = false, defaultValue = "") String keptCookie) {
        return bookService.getPageableBooksByTag(offset, limit, tag, cartCookie, keptCookie);
    }

    @GetMapping("/genre/{slug}")
    public BookListDto booksByGenre(@RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset,
                                    @RequestParam(value = "limit", required = false, defaultValue = "20") Integer limit,
                                    @CookieValue(value = "cartContent", required = false, defaultValue = "") String cartCookie,
                                    @CookieValue(value = "keptContent", required = false, defaultValue = "") String keptCookie,
                                    @PathVariable String slug) {
        var genre = genreService.getGenreBySlug(slug);
        return bookService.getPageableBooksByGenre(offset, limit, genre, cartCookie, keptCookie);
    }

    @ExecutionTimeLog
    @PostMapping("/rateBook")
    public CommonResultDto rateBook(@RequestBody BookRateRequestDto bookRateRequestDto) {

        var result = true;
        try {
            bookVoteService.rateBook(userService.getCurrentUser(), bookService.getBookEntityBySlug(bookRateRequestDto.getBookId()), bookRateRequestDto.getValue());
        } catch (Exception e) {
            log.warn("Book vote error: {}", e.getMessage());
            result = false;
        }
        return new CommonResultDto(result);
    }

    @PostMapping("/bookReview")
    public CommonResultDto reviewBook(@RequestBody BookReviewRequestDto dto) {
        return bookReviewService.saveBookReview(
                bookService.getBookEntityBySlug(dto.getBookId()), dto.getText(), userService.getCurrentUser());
    }

    @PostMapping("/rateBookReview")
    public CommonResultDto rateBookReview(@RequestBody BookReviewLikeRequestDto dto) {
        var result = bookReviewLikeService.saveBookReviewLike(dto, userService.getCurrentUser());
        return new CommonResultDto(result);
    }
}

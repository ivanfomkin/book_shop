package com.github.ivanfomkin.bookshop.service.impl;

import com.github.ivanfomkin.bookshop.dto.cart.CartBookElementDto;
import com.github.ivanfomkin.bookshop.dto.cart.CartDto;
import com.github.ivanfomkin.bookshop.entity.book.BookEntity;
import com.github.ivanfomkin.bookshop.entity.enums.Book2UserType;
import com.github.ivanfomkin.bookshop.entity.user.UserEntity;
import com.github.ivanfomkin.bookshop.service.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    private final BookService bookService;
    private final AuthorService authorService;
    private final CookieService cookieService;
    private final Book2UserService book2UserService;

    public CartServiceImpl(BookService bookService, AuthorService authorService, CookieService cookieService, Book2UserService book2UserService) {
        this.bookService = bookService;
        this.authorService = authorService;
        this.cookieService = cookieService;
        this.book2UserService = book2UserService;
    }

    @Override
    public CartDto getCartDtoByUser(UserEntity user) {
        if (user == null) {
            return new CartDto(List.of(), 0, 0);
        }
        var books = bookService.getBooksByUserAndType(user, Book2UserType.CART);
        return getCartDtoFromBookList(books);
    }

    @Override
    public CartDto getCartDtoFromCookie(String cookieValue) {
        if (cookieValue == null || cookieValue.isEmpty()) {
            return new CartDto(List.of(), 0, 0);
        } else {
            var bookSlugList = cookieService.getBookSlugListFromCookie(cookieValue);
            var bookEntityListBySlug = bookService.getBooksBySlugIn(bookSlugList);
            return getCartDtoFromBookList(bookEntityListBySlug);
        }
    }


    @Override
    public List<CartBookElementDto> getPostponedBooksByUser(UserEntity user) {
        if (user == null) {
            return List.of();
        }
        var books = bookService.getBooksByUserAndType(user, Book2UserType.KEPT);
        return convertBooksToDto(books);
    }

    @Override
    public List<CartBookElementDto> getPostponedBooksFromCookie(String cookieValue) {
        if (cookieValue == null || cookieValue.isEmpty()) {
            return List.of();
        } else {
            var bookSlugList = cookieService.getBookSlugListFromCookie(cookieValue);
            var bookEntityList = bookService.getBooksBySlugIn(bookSlugList);
            return convertBooksToDto(bookEntityList);
        }
    }

    @Override
    public void mergeCartWithUser(String cartCookie, UserEntity user) {
        mergeBookCookieWithDatabase(cartCookie, user, Book2UserType.CART);
    }

    @Override
    public void mergeKeptWithUser(String keptCookie, UserEntity user) {
        mergeBookCookieWithDatabase(keptCookie, user, Book2UserType.KEPT);
    }

    private void mergeBookCookieWithDatabase(String cookie, UserEntity user, Book2UserType status) {
        if (cookie == null || cookie.isEmpty()) {
            return;
        }
        var bookEntityList = bookService.getBooksBySlugIn(cookieService.getBookSlugListFromCookie(cookie));
        for (BookEntity book : bookEntityList) {
            book2UserService.changeBookStatus(user, book, status);
        }
    }

    private CartDto getCartDtoFromBookList(List<BookEntity> bookEntities) {
        var cartAmount = bookEntities.stream().mapToInt(b -> bookService.calculateBookDiscountPrice(b.getPrice(), b.getDiscount())).sum();
        var cartAmountOld = bookEntities.stream().mapToInt(BookEntity::getPrice).sum();
        var bookDtoList = convertBooksToDto(bookEntities);
        return new CartDto(bookDtoList, cartAmount, cartAmountOld);
    }

    private List<CartBookElementDto> convertBooksToDto(List<BookEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            return new ArrayList<>();
        } else {
            return entities.stream().map(this::convertOneBookToDto).toList();
        }
    }

    private CartBookElementDto convertOneBookToDto(BookEntity bookEntity) {
        return new CartBookElementDto(
                bookEntity.getTitle(),
                bookEntity.getSlug(),
                bookEntity.getImage(),
                bookEntity.getPrice(),
                authorService.convertAuthorsToDto(bookEntity.getAuthors()),
                bookEntity.getDiscount(),
                bookEntity.getIsBestseller(),
                "false", // TODO: 10.01.2022 тут заглушка. Исправить
                bookService.calculateBookDiscountPrice(bookEntity.getPrice(), bookEntity.getDiscount())
        );
    }
}

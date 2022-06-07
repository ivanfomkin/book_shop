package com.example.MyBookShopApp.service.impl;

import com.example.MyBookShopApp.dto.cart.CartBookElementDto;
import com.example.MyBookShopApp.dto.cart.CartDto;
import com.example.MyBookShopApp.entity.book.BookEntity;
import com.example.MyBookShopApp.entity.enums.Book2UserType;
import com.example.MyBookShopApp.entity.user.UserEntity;
import com.example.MyBookShopApp.service.AuthorService;
import com.example.MyBookShopApp.service.BookService;
import com.example.MyBookShopApp.service.CartService;
import com.example.MyBookShopApp.service.CookieService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    private final BookService bookService;
    private final AuthorService authorService;
    private final CookieService cookieService;

    public CartServiceImpl(BookService bookService, AuthorService authorService, CookieService cookieService) {
        this.bookService = bookService;
        this.authorService = authorService;
        this.cookieService = cookieService;
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

    private CartDto getCartDtoFromBookList(List<BookEntity> bookEntities) {
        var cartAmount = bookEntities.stream().mapToInt(b -> bookService.calculateBookDiscountPrice(b.getPrice(), b.getDiscount())).sum();
        var cartAmountOld = bookEntities.stream().mapToInt(BookEntity::getPrice).sum();
        var bookDtoList = convertBooksToDto(bookEntities);
        return new CartDto(bookDtoList, cartAmount, cartAmountOld);
    }

    private List<CartBookElementDto> convertBooksToDto(List<BookEntity> entities) {
        if (entities == null || entities.size() == 0) {
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

package com.example.MyBookShopApp.service.impl;

import com.example.MyBookShopApp.dto.AuthorElementDto;
import com.example.MyBookShopApp.dto.CartBookElementDto;
import com.example.MyBookShopApp.dto.CartDto;
import com.example.MyBookShopApp.entity.author.AuthorEntity;
import com.example.MyBookShopApp.entity.book.BookEntity;
import com.example.MyBookShopApp.service.BookService;
import com.example.MyBookShopApp.service.CartService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    private final BookService bookService;

    public CartServiceImpl(BookService bookService) {
        this.bookService = bookService;
    }

    @Override
    public CartDto getCartDtoByCookie(String cookie) {
        cookie = cookie.startsWith("/") ? cookie.substring(1) : cookie;
        cookie = cookie.endsWith("/") ? cookie.substring(0, cookie.length() - 1) : cookie;
        String[] cookieSlug = cookie.split("/");
        var books = bookService.getBooksBySlugIn(cookieSlug);
        var bookPrice = books.stream().mapToInt(b -> bookService.calculateBookDiscountPrice(b.getPrice(), b.getDiscount())).sum();
        var bookPriceOld = books.stream().mapToInt(BookEntity::getPrice).sum();
        return new CartDto(convertBooksToDto(books), bookPrice, bookPriceOld);
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
                convertAuthorsToDto(bookEntity.getAuthors()),
                bookEntity.getDiscount(),
                bookEntity.getIsBestseller(),
                "false", // TODO: 10.01.2022 тут заглушка. Исправить
                bookService.calculateBookDiscountPrice(bookEntity.getPrice(), bookEntity.getDiscount())
        );
    }

    private List<AuthorElementDto> convertAuthorsToDto(List<AuthorEntity> authorEntities) {
        List<AuthorElementDto> authorElementDtoList = new ArrayList<>();
        for (AuthorEntity authorEntity : authorEntities) {
            authorElementDtoList.add(new AuthorElementDto(authorEntity.getName(), authorEntity.getSlug()));
        }
        return authorElementDtoList;
    }
}

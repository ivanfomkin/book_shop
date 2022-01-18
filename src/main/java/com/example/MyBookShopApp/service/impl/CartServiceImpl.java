package com.example.MyBookShopApp.service.impl;

import com.example.MyBookShopApp.dto.cart.CartBookElementDto;
import com.example.MyBookShopApp.dto.cart.CartDto;
import com.example.MyBookShopApp.entity.book.BookEntity;
import com.example.MyBookShopApp.entity.enums.Book2UserType;
import com.example.MyBookShopApp.entity.user.UserEntity;
import com.example.MyBookShopApp.repository.Book2UserRepository;
import com.example.MyBookShopApp.repository.Book2UserTypeRepository;
import com.example.MyBookShopApp.service.AuthorService;
import com.example.MyBookShopApp.service.BookService;
import com.example.MyBookShopApp.service.CartService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    private final BookService bookService;
    private final AuthorService authorService;
    private final Book2UserRepository book2UserRepository;
    private final Book2UserTypeRepository book2UserTypeRepository;

    public CartServiceImpl(BookService bookService, AuthorService authorService, Book2UserRepository book2UserRepository, Book2UserTypeRepository book2UserTypeRepository) {
        this.bookService = bookService;
        this.authorService = authorService;
        this.book2UserRepository = book2UserRepository;
        this.book2UserTypeRepository = book2UserTypeRepository;
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
    public List<CartBookElementDto> getPostponedBooks(UserEntity user) {
        if (user == null) {
            return List.of();
        }
        var books = bookService.getBooksByUserAndType(user, Book2UserType.KEPT);
        return convertBooksToDto(books);
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

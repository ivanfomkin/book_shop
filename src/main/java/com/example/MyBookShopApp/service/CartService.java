package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.dto.cart.CartDto;
import com.example.MyBookShopApp.entity.enums.Book2UserType;
import com.example.MyBookShopApp.entity.user.UserEntity;

public interface CartService {
    CartDto getCartDtoByUser(UserEntity user);

    void deleteBookFromCart(UserEntity user, String bookSlug);

    void addBookToCart(UserEntity userBySession, Book2UserType status, String slug);

    int getCartAmount(UserEntity user);
}

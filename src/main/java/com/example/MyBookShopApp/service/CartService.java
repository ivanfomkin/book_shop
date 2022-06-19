package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.dto.cart.CartBookElementDto;
import com.example.MyBookShopApp.dto.cart.CartDto;
import com.example.MyBookShopApp.entity.user.UserEntity;

import java.util.List;

public interface CartService {
    CartDto getCartDtoByUser(UserEntity user);

    CartDto getCartDtoFromCookie(String cookieValue);

    List<CartBookElementDto> getPostponedBooksByUser(UserEntity user);

    List<CartBookElementDto> getPostponedBooksFromCookie(String cookieValue);

    void mergeCartWithUser(String cartCookie, UserEntity user);

    void mergeKeptWithUser(String keptCookie, UserEntity user);
}

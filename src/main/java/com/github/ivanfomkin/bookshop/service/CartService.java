package com.github.ivanfomkin.bookshop.service;

import com.github.ivanfomkin.bookshop.dto.cart.CartBookElementDto;
import com.github.ivanfomkin.bookshop.dto.cart.CartDto;
import com.github.ivanfomkin.bookshop.entity.user.UserEntity;

import java.util.List;

public interface CartService {
    CartDto getCartDtoByUser(UserEntity user);

    CartDto getCartDtoFromCookie(String cookieValue);

    List<CartBookElementDto> getPostponedBooksByUser(UserEntity user);

    List<CartBookElementDto> getPostponedBooksFromCookie(String cookieValue);

    void mergeCartWithUser(String cartCookie, UserEntity user);

    void mergeKeptWithUser(String keptCookie, UserEntity user);
}

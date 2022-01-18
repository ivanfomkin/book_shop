package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.dto.cart.CartBookElementDto;
import com.example.MyBookShopApp.dto.cart.CartDto;
import com.example.MyBookShopApp.entity.user.UserEntity;

import java.util.List;

public interface CartService {
    CartDto getCartDtoByUser(UserEntity user);

    List<CartBookElementDto> getPostponedBooks(UserEntity user);
}

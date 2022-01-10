package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.dto.CartDto;

public interface CartService {
    CartDto getCartDtoByCookie(String cookie);
}

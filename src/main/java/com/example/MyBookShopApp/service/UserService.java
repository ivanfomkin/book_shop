package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.entity.user.UserEntity;

import javax.servlet.http.HttpSession;

public interface UserService {
    UserEntity getUserBySession(HttpSession session);
}

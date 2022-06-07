package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.dto.cart.ChangeBookStatusRequestDto;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface CookieService {

    int getCookieSize(String cookie);

    void changeBookStatus(Cookie[] cookies, HttpServletResponse response, ChangeBookStatusRequestDto dto);

    List<String> getBookSlugListFromCookie(String cookie);
}

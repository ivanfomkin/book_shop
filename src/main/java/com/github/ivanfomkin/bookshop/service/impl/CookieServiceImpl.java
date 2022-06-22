package com.github.ivanfomkin.bookshop.service.impl;

import com.github.ivanfomkin.bookshop.dto.cart.ChangeBookStatusRequestDto;
import com.github.ivanfomkin.bookshop.service.CookieService;
import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

@Service
public class CookieServiceImpl implements CookieService {
    private static final String CART_COOKIE_NAME = "cartContent";
    private static final String KEPT_COOKIE_NAME = "keptContent";

    @Override
    public int getCookieSize(String cookieValue) {
        if (cookieValue == null || cookieValue.isEmpty()) {
            return 0;
        } else {
            return cookieValue.split("/").length;
        }
    }

    @Override
    public void changeBookStatus(Cookie[] cookies, HttpServletResponse response, ChangeBookStatusRequestDto dto) {
        var cartCookie = getCookieByName(cookies, CART_COOKIE_NAME);
        var keptCookie = getCookieByName(cookies, KEPT_COOKIE_NAME);
        switch (dto.getStatus()) {
            case CART -> {
                addBookToCookie(dto.getSlug(), cartCookie);
                removeBookFromCookie(dto.getSlug(), keptCookie);
            }
            case KEPT -> {
                addBookToCookie(dto.getSlug(), keptCookie);
                removeBookFromCookie(dto.getSlug(), cartCookie);
            }
            case UNLINK -> {
                removeBookFromCookie(dto.getSlug(), cartCookie);
                removeBookFromCookie(dto.getSlug(), keptCookie);
            }
            default ->
                    throw new NotImplementedException(MessageFormatter.format("Status change for {} not implemented", dto.getStatus()).getMessage());
        }
        response.addCookie(cartCookie);
        response.addCookie(keptCookie);
    }

    @Override
    public List<String> getBookSlugListFromCookie(String cookie) {
        if (cookie == null || cookie.isEmpty()) {
            return new ArrayList<>();
        } else {
            return new ArrayList<>(Arrays.asList(cookie.split("/")));
        }
    }

    private void addBookToCookie(String bookSlug, Cookie cookie) {
        if (cookie.getValue() == null || cookie.getValue().isEmpty()) {
            cookie.setValue(bookSlug);
        } else if (!cookie.getValue().contains(bookSlug)) {
            StringJoiner stringJoiner = new StringJoiner("/");
            stringJoiner.add(cookie.getValue()).add(bookSlug);
            cookie.setValue(stringJoiner.toString());
        }
    }

    private void removeBookFromCookie(String bookSlug, Cookie cookie) {
        if (cookie.getValue() != null && !cookie.getValue().isEmpty()) {
            var bookSlugs = getBookSlugListFromCookie(cookie.getValue());
            bookSlugs.remove(bookSlug);
            cookie.setValue(String.join("/", bookSlugs));
        }
    }

    private Cookie getCookieByName(Cookie[] cookies, String name) {
        if (cookies != null && cookies.length != 0) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equalsIgnoreCase(name)) {
                    cookie.setPath("/");
                    return cookie;
                }
            }
        }
        return createCookie(name);
    }

    private Cookie createCookie(String name) {
        Cookie cookie = new Cookie(name, "");
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        return cookie;
    }
}

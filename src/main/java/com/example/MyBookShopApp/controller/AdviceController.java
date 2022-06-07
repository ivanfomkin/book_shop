package com.example.MyBookShopApp.controller;

import com.example.MyBookShopApp.exception.EmptySearchQueryException;
import com.example.MyBookShopApp.exception.JwtInBlackListException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class AdviceController {

    @ExceptionHandler(EmptySearchQueryException.class)
    public String handleEmptySearchQueryException(EmptySearchQueryException exception, RedirectAttributes redirectAttributes) {
        log.error(exception.getMessage());
        redirectAttributes.addFlashAttribute("searchError", exception);
        return "redirect:/";
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public String handleExpiredToken(ExpiredJwtException exception, HttpServletResponse response) {
        log.debug("JWT token expired: {}", exception.getMessage());
        clearContextAndCookie(response);
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        return "redirect:/signin";
    }

    @ExceptionHandler(JwtException.class)
    public String handleJwtException(JwtException exception, HttpServletResponse response) {
        log.debug("JWT token exception: {}", exception.getMessage());
        clearContextAndCookie(response);
        return "redirect:/signin";
    }

    @ExceptionHandler(JwtInBlackListException.class)
    public String handleJwtInBlackListException(JwtInBlackListException exception, HttpServletResponse response) {
        log.warn("JWT token in blacklist: {}", exception.getMessage());
        clearContextAndCookie(response);
        return "redirect:/signin";
    }

    @ExceptionHandler(SignatureException.class)
    public String handleSignatureException(SignatureException exception, HttpServletResponse response) {
        log.warn("Bad JWT signature:: {}", exception.getMessage());
        clearContextAndCookie(response);
        return "redirect:/signin";
    }

    private void clearContextAndCookie(HttpServletResponse response) {
        Cookie tokenCookie = new Cookie("token", null);
        tokenCookie.setMaxAge(0);
        response.addCookie(tokenCookie);
        SecurityContextHolder.clearContext();
    }
}

package com.github.ivanfomkin.bookshop.controller;

import com.github.ivanfomkin.bookshop.dto.CommonResultDto;
import com.github.ivanfomkin.bookshop.exception.*;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@ControllerAdvice
public class AdviceController {

    private static final String REDIRECT_SIGNING_URL = "redirect:/signin";

    @ExceptionHandler(EmptySearchQueryException.class)
    public String handleEmptySearchQueryException(EmptySearchQueryException exception, RedirectAttributes redirectAttributes) {
        log.error(exception.getMessage());
        redirectAttributes.addFlashAttribute("searchError", exception);
        return "redirect:/";
    }

    @ExceptionHandler(PasswordException.class)
    public String handlePasswordsDidNotMatch(PasswordException exception, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("passwordError", exception.getLocalizedMessage());
        return "redirect:/profile";
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public String handleExpiredToken(ExpiredJwtException exception, HttpServletResponse response) {
        log.debug("JWT token expired: {}", exception.getMessage());
        clearContextAndCookie(response);
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        return REDIRECT_SIGNING_URL;
    }

    @ExceptionHandler(JwtException.class)
    public String handleJwtException(JwtException exception, HttpServletResponse response) {
        log.debug("JWT token exception: {}", exception.getMessage());
        clearContextAndCookie(response);
        return REDIRECT_SIGNING_URL;
    }

    @ExceptionHandler(JwtInBlackListException.class)
    public String handleJwtInBlackListException(JwtInBlackListException exception, HttpServletResponse response) {
        log.warn("JWT token in blacklist: {}", exception.getMessage());
        clearContextAndCookie(response);
        return REDIRECT_SIGNING_URL;
    }

    @ExceptionHandler(SignatureException.class)
    public String handleSignatureException(SignatureException exception, HttpServletResponse response) {
        log.warn("Bad JWT signature:: {}", exception.getMessage());
        clearContextAndCookie(response);
        return REDIRECT_SIGNING_URL;
    }

    @ResponseBody
    @ExceptionHandler(BookCartIsEmptyException.class)
    public CommonResultDto handleEmptyCartException() {
        log.warn("Try to order for empty book cart");
        var resultDto = new CommonResultDto(false);
        resultDto.setError("Корзина пуста");
        return resultDto;
    }

    @ResponseBody
    @ExceptionHandler(NotAuthorizedException.class)
    public CommonResultDto handleNotAuthorizedException() {
        log.warn("Try to order book when user not authorize");
        var resultDto = new CommonResultDto(false);
        resultDto.setError("Авторизуйтесь для покупки книг");
        return resultDto;
    }

    @ResponseBody
    @ExceptionHandler(InsufficientFundsException.class)
    public CommonResultDto handleInsufficientFundsException(InsufficientFundsException exception) {
        log.warn(exception.getMessage());
        var resultDto = new CommonResultDto(false);
        resultDto.setError("Недостаточно средств на счёте для покупки");
        return resultDto;
    }

    private void clearContextAndCookie(HttpServletResponse response) {
        Cookie tokenCookie = new Cookie("token", null);
        tokenCookie.setMaxAge(0);
        response.addCookie(tokenCookie);
        SecurityContextHolder.clearContext();
    }
}

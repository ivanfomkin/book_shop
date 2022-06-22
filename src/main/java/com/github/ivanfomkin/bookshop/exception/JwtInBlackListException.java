package com.github.ivanfomkin.bookshop.exception;

import io.jsonwebtoken.JwtException;

public class JwtInBlackListException extends JwtException {
    public JwtInBlackListException(String message) {
        super(message);
    }

    public JwtInBlackListException(String message, Throwable cause) {
        super(message, cause);
    }
}

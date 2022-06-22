package com.github.ivanfomkin.bookshop.exception;

public class EmptySearchQueryException extends RuntimeException {
    public EmptySearchQueryException(String s) {
        super(s);
    }
}

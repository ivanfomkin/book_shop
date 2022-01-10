package com.example.MyBookShopApp.exception;

public class EmptySearchQueryException extends RuntimeException {
    public EmptySearchQueryException(String s) {
        super(s);
    }
}

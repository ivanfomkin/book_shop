package com.github.ivanfomkin.bookshop.exception;

import java.util.EmptyStackException;

public class BookCartIsEmptyException extends EmptyStackException {
    public BookCartIsEmptyException() {
        super();
    }
}

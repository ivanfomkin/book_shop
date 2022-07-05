package com.github.ivanfomkin.bookshop.exception;

public class PasswordsDidNotMatchException extends PasswordException{
    public PasswordsDidNotMatchException(String s) {
        super(s);
    }
}

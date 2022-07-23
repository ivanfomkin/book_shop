package com.github.ivanfomkin.bookshop.exception;

import javax.persistence.EntityNotFoundException;

public class TransactionNotFoundException extends EntityNotFoundException {
    public TransactionNotFoundException(String message) {
        super(message);
    }
}

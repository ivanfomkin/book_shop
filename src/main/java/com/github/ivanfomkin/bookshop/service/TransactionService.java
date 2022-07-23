package com.github.ivanfomkin.bookshop.service;

import com.github.ivanfomkin.bookshop.entity.enums.TransactionType;
import com.github.ivanfomkin.bookshop.entity.payments.TransactionEntity;

public interface TransactionService {
    TransactionEntity createTransaction(double amount, TransactionType type);

    TransactionEntity findTransactionById(long id);

    void setTransactionResult(TransactionEntity transaction, boolean paymentResult);
}

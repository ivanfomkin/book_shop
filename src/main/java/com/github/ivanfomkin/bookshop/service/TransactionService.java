package com.github.ivanfomkin.bookshop.service;

import com.github.ivanfomkin.bookshop.dto.transaction.TransactionHistoryDto;
import com.github.ivanfomkin.bookshop.entity.enums.TransactionType;
import com.github.ivanfomkin.bookshop.entity.payments.TransactionEntity;
import com.github.ivanfomkin.bookshop.entity.user.UserEntity;

import java.util.List;

public interface TransactionService {
    TransactionEntity createTransaction(double amount, TransactionType type);

    TransactionEntity findTransactionById(long id);

    void setTransactionResult(TransactionEntity transaction, boolean paymentResult);

    List<TransactionHistoryDto> getTransactionHistoryByUser(UserEntity user);
}

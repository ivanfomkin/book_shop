package com.github.ivanfomkin.bookshop.service.impl;

import com.github.ivanfomkin.bookshop.entity.enums.TransactionType;
import com.github.ivanfomkin.bookshop.entity.payments.TransactionEntity;
import com.github.ivanfomkin.bookshop.exception.TransactionNotFoundException;
import com.github.ivanfomkin.bookshop.repository.TransactionRepository;
import com.github.ivanfomkin.bookshop.service.TransactionService;
import com.github.ivanfomkin.bookshop.service.UserService;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class TransactionServiceImpl implements TransactionService {
    private final UserService userService;
    private final TransactionRepository transactionRepository;

    public TransactionServiceImpl(UserService userService, TransactionRepository transactionRepository) {
        this.userService = userService;
        this.transactionRepository = transactionRepository;
    }

    @Override
    @Transactional
    public TransactionEntity createTransaction(double amount, TransactionType type) {
        var transaction = new TransactionEntity();
        transaction.setType(type);
        transaction.setUser(userService.getCurrentUser());
        transaction.setAmount(amount);
        return transactionRepository.save(transaction);
    }

    @Override
    public TransactionEntity findTransactionById(long id) {
        return transactionRepository.findById(id).orElseThrow(() -> new TransactionNotFoundException(MessageFormatter.format("Balance transaction with id {} not found", id).getMessage()));
    }

    @Override
    @Transactional
    public void setTransactionResult(TransactionEntity transaction, boolean paymentResult) {
        transaction.setStatus(paymentResult);
        transaction.setApproveDate(LocalDateTime.now());
        transactionRepository.save(transaction);
    }
}

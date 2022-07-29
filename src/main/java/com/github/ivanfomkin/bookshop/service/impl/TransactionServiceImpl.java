package com.github.ivanfomkin.bookshop.service.impl;

import com.github.ivanfomkin.bookshop.dto.transaction.BookTransactionInfoDto;
import com.github.ivanfomkin.bookshop.dto.transaction.TransactionHistoryDto;
import com.github.ivanfomkin.bookshop.entity.book.BookEntity;
import com.github.ivanfomkin.bookshop.entity.enums.TransactionType;
import com.github.ivanfomkin.bookshop.entity.payments.BookOrderEntity;
import com.github.ivanfomkin.bookshop.entity.payments.TransactionEntity;
import com.github.ivanfomkin.bookshop.entity.user.UserEntity;
import com.github.ivanfomkin.bookshop.exception.InsufficientFundsException;
import com.github.ivanfomkin.bookshop.exception.TransactionNotFoundException;
import com.github.ivanfomkin.bookshop.repository.TransactionRepository;
import com.github.ivanfomkin.bookshop.service.TransactionService;
import com.github.ivanfomkin.bookshop.service.UserService;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {
    private final UserService userService;
    private final TransactionRepository transactionRepository;

    public TransactionServiceImpl(UserService userService, TransactionRepository transactionRepository) {
        this.userService = userService;
        this.transactionRepository = transactionRepository;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public TransactionEntity createTransaction(double amount, TransactionType type) {
        UserEntity currentUser = userService.getCurrentUser();
        if (type == TransactionType.DEBIT && amount > currentUser.getBalance()) {
            throw new InsufficientFundsException(MessageFormatter.arrayFormat("Insufficient funds for debt {} money point from userId {}. User balance is", new Object[]{amount, currentUser.getId(), currentUser.getBalance()}).getMessage());
        }
        var transaction = new TransactionEntity();
        transaction.setType(type);
        transaction.setUser(currentUser);
        transaction.setAmount(amount);
        if (type.equals(TransactionType.DEBIT)) {
            transaction.setApproveDate(LocalDateTime.now());
            transaction.setStatus(true);
        }
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

    @Override
    public List<TransactionHistoryDto> getTransactionHistoryByUser(UserEntity user) {
        var transactions = transactionRepository.findAllByUser(user);
        return convertEntitiesToDto(transactions);
    }

    private List<TransactionHistoryDto> convertEntitiesToDto(List<TransactionEntity> entities) {
        return entities.parallelStream().map(e -> {
            var transactionDto = new TransactionHistoryDto();
            transactionDto.setDate(e.getTransactionDate());
            transactionDto.setAmount(e.getAmount());
            transactionDto.setType(e.getType());
            if (e.getBookOrders() != null && !e.getBookOrders().isEmpty()) {
                var books = e.getBookOrders().parallelStream().map(BookOrderEntity::getBook).toList();
                transactionDto.setBooks(convertBooksToBookTransactionInfoDto(books));
            }
            return transactionDto;
        }).toList();
    }

    private List<BookTransactionInfoDto> convertBooksToBookTransactionInfoDto(List<BookEntity> books) {
        return books.parallelStream().map(BookTransactionInfoDto::new).toList();
    }
}

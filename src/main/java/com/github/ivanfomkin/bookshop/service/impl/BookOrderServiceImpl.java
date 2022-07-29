package com.github.ivanfomkin.bookshop.service.impl;

import com.github.ivanfomkin.bookshop.entity.book.BookEntity;
import com.github.ivanfomkin.bookshop.entity.payments.BookOrderEntity;
import com.github.ivanfomkin.bookshop.entity.payments.TransactionEntity;
import com.github.ivanfomkin.bookshop.repository.BookOrderRepository;
import com.github.ivanfomkin.bookshop.service.BookOrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookOrderServiceImpl implements BookOrderService {
    private final BookOrderRepository bookOrderRepository;

    public BookOrderServiceImpl(BookOrderRepository bookOrderRepository) {
        this.bookOrderRepository = bookOrderRepository;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void createBookOrders(List<BookEntity> books, TransactionEntity transaction) {
        List<BookOrderEntity> newOrders = books.parallelStream().map(b -> {
            var bookOrder = new BookOrderEntity();
            bookOrder.setBook(b);
            bookOrder.setTransaction(transaction);
            return bookOrder;
        }).toList();
        bookOrderRepository.saveAll(newOrders);
    }
}

package com.github.ivanfomkin.bookshop.service;

import com.github.ivanfomkin.bookshop.entity.book.BookEntity;
import com.github.ivanfomkin.bookshop.entity.payments.TransactionEntity;

import java.util.List;

public interface BookOrderService {
    void createBookOrders(List<BookEntity> books, TransactionEntity transaction);
}

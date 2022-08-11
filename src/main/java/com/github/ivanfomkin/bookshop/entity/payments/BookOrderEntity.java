package com.github.ivanfomkin.bookshop.entity.payments;

import com.github.ivanfomkin.bookshop.entity.book.BookEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "book_orders")
public class BookOrderEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "book_id")
    private BookEntity book;
    @ManyToOne
    @JoinColumn(name = "transaction_id")
    private TransactionEntity transaction;
}

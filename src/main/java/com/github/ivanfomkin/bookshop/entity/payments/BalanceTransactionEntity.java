package com.github.ivanfomkin.bookshop.entity.payments;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "balance_transaction")
public class BalanceTransactionEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "INT NOT NULL")
    private Integer userId;

    @Column(columnDefinition = "TIMESTAMP NOT NULL")
    private LocalDateTime time;

    @Column(columnDefinition = "INT NOT NULL  DEFAULT 0")
    private Integer value;

    @Column(columnDefinition = "INT NOT NULL")
    private Integer bookId;

    @Column(columnDefinition = "TEXT NOT NULL")
    private String description;

}

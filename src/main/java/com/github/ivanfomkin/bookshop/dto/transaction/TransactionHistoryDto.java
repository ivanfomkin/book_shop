package com.github.ivanfomkin.bookshop.dto.transaction;

import com.github.ivanfomkin.bookshop.entity.enums.TransactionType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TransactionHistoryDto {
    private double amount;
    private LocalDateTime date;
    private TransactionType type;
    private List<BookTransactionInfoDto> books;
}

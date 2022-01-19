package com.example.MyBookShopApp.dto.book.rate;

import lombok.Data;

@Data
public class BookRateRequestDto {
    private short value;
    private String bookId;
}

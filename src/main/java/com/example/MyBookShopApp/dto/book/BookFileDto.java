package com.example.MyBookShopApp.dto.book;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookFileDto {
    private String hash;
    private String extension;
}

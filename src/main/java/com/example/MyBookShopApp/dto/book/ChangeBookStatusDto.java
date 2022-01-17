package com.example.MyBookShopApp.dto.book;

import com.example.MyBookShopApp.entity.enums.Book2UserType;
import lombok.Data;

import java.util.List;

@Data
public class ChangeBookStatusDto {
    private List<String> booksIds;
    private Book2UserType status;
}

package com.example.MyBookShopApp.dto.cart;

import com.example.MyBookShopApp.entity.enums.Book2UserType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ChangeBookStatusRequestDto {
    @JsonProperty("booksIds")
    private String slug;
    private Book2UserType status;
}

package com.github.ivanfomkin.bookshop.dto.cart;

import com.github.ivanfomkin.bookshop.entity.enums.Book2UserType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ChangeBookStatusRequestDto {
    @JsonProperty("booksIds")
    private String slugs;
    private Book2UserType status;
}

package com.example.MyBookShopApp.dto.cart;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CartDto(List<CartBookElementDto> books, int cartPrice, int cartPriceOld) {
}

package com.example.MyBookShopApp.dto.search;

public record SearchDto(String searchQuery) {
    public SearchDto() {
        this("");
    }
}

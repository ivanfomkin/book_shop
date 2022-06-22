package com.github.ivanfomkin.bookshop.dto.search;

public record SearchDto(String searchQuery) {
    public SearchDto() {
        this("");
    }
}

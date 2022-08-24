package com.github.ivanfomkin.bookshop.dto.book;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookGiftDto {
    private String title;
    private String author;
    private String slug;

    public String getFullTitle() {
        return title + " | " + author;
    }
}

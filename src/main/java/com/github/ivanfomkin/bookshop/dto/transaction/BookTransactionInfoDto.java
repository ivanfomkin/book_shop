package com.github.ivanfomkin.bookshop.dto.transaction;

import com.github.ivanfomkin.bookshop.entity.book.BookEntity;
import lombok.Data;

@Data
public class BookTransactionInfoDto {
    private String slug;
    private String title;

    public BookTransactionInfoDto(BookEntity book) {
        this.slug = book.getSlug();
        this.title = book.getTitle();
    }
}

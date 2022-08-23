package com.github.ivanfomkin.bookshop.dto.author;

import com.github.ivanfomkin.bookshop.dto.book.BookListElement;
import lombok.Data;

import java.util.List;

@Data
public class AuthorListDto {
    private long total;
    private List<AuthorElementDto> authors;
    private long perPage;
    private long page;
}

package com.github.ivanfomkin.bookshop.dto.author;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthorElementDto {
    private int id;
    private String name;
    private String slug;
}

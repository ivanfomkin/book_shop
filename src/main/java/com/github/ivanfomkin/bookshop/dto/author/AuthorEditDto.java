package com.github.ivanfomkin.bookshop.dto.author;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class AuthorEditDto {
    private int id;
    private String name;
    private MultipartFile photo;
    private String slug;
    private String description;
}

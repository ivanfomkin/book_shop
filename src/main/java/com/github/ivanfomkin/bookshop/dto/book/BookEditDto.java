package com.github.ivanfomkin.bookshop.dto.book;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class BookEditDto {
    private int id;
    private String slug;
    private String title;
    private String[] authorSlug;
    private String[] tags;
    private String[] genreSlug;
    private boolean isBestseller;
    private String description;
    private int price;
    private short discount;
    private MultipartFile bookImage;
    private MultipartFile pdfFile;
    private MultipartFile fb2File;
    private MultipartFile epubFile;
}

package com.example.MyBookShopApp.dto.book;

import com.example.MyBookShopApp.dto.author.AuthorElementDto;
import lombok.Data;

import java.util.List;

@Data
public class BookSlugDto {
    private String title;
    private String image;
    private List<AuthorElementDto> authors;
    private Integer price;
    private Integer discountPrice;
    private String description;
    private String slug;
    private BookRatingInfoDto rating;
    private List<String> tags;
    private List<BookFileDto> files;
}

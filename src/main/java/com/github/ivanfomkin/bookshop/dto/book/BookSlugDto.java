package com.github.ivanfomkin.bookshop.dto.book;

import com.github.ivanfomkin.bookshop.dto.author.AuthorElementDto;
import com.github.ivanfomkin.bookshop.dto.book.review.BookReviewDto;
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
    private BookReviewDto reviews;
    private String status;
}

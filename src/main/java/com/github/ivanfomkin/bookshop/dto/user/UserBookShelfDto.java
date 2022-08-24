package com.github.ivanfomkin.bookshop.dto.user;

import com.github.ivanfomkin.bookshop.dto.review.BookSlugInfoDto;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserBookShelfDto {
    private int book2UserId;
    private BookSlugInfoDto book;
    private String status;
    private LocalDateTime statusDate;
}

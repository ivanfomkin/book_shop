package com.github.ivanfomkin.bookshop.dto.book;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookListElement {
    private int id;
    private String slug;
    private String image;
    private String authors;
    private String title;
    private Short discount;
    private boolean isBestseller;
    private Object rating;
    private String status;
    private int price;
    private int discountPrice;
}

package com.example.MyBookShopApp.dto.book;

import com.example.MyBookShopApp.dto.Dto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookDto implements Dto {
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

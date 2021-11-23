package com.example.MyBookShopApp.model;

import lombok.Data;

@Data
public class Book {
    private Integer id;
    private String author;
    private String title;
    private Float priceOld;
    private Float price;
}

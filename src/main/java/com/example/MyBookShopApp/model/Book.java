package com.example.MyBookShopApp.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private Float priceOld;
    private Float price;
    @ManyToOne
    @JoinColumn(name = "author_id")
    private Author author;
}

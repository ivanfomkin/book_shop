package com.example.MyBookShopApp.entity.book;

import com.example.MyBookShopApp.entity.author.AuthorEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "books")
public class BookEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "pub_date", columnDefinition = "DATE NOT NULL")
    private LocalDate publishDate;

    @Column(columnDefinition = "SMALLINT NOT NULL")
    private Boolean isBestseller;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String slug;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String title;

    @Column(columnDefinition = "VARCHAR(255)")
    private String image;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "INT NOT NULL")
    private Integer price;

    @Column(columnDefinition = "SMALLINT NOT NULL DEFAULT 0")
    private Byte discount;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private AuthorEntity author;
}

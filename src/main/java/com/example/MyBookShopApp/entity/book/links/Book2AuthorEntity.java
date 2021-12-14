package com.example.MyBookShopApp.entity.book.links;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "book2author")
public class Book2AuthorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "book_id", columnDefinition = "INT NOT NULL")
    private Integer bookId;

    @Column(name = "author_id", columnDefinition = "INT NOT NULL")
    private Integer authorId;

    @Column(columnDefinition = "INT NOT NULL  DEFAULT 0")
    private Integer sortIndex;

}

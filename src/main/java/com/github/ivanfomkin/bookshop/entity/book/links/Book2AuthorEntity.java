package com.github.ivanfomkin.bookshop.entity.book.links;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "book2author")
public class Book2AuthorEntity implements Serializable {

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

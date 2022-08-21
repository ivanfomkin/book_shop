package com.github.ivanfomkin.bookshop.entity.book.links;

import com.github.ivanfomkin.bookshop.entity.author.AuthorEntity;
import com.github.ivanfomkin.bookshop.entity.book.BookEntity;
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

    @ManyToOne
    private BookEntity book;

    @ManyToOne
    private AuthorEntity author;

    @Column(columnDefinition = "INT NOT NULL  DEFAULT 0")
    private Integer sortIndex;

}

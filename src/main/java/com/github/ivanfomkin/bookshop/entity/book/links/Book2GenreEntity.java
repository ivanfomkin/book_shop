package com.github.ivanfomkin.bookshop.entity.book.links;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "book2genre")
public class Book2GenreEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "book_id", columnDefinition = "INT NOT NULL")
    private Integer bookId;

    @Column(name = "genre_id", columnDefinition = "INT NOT NULL")
    private Integer genreId;
}

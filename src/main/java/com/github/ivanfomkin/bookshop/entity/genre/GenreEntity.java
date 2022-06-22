package com.github.ivanfomkin.bookshop.entity.genre;

import com.github.ivanfomkin.bookshop.entity.book.BookEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "genre")
public class GenreEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private GenreEntity parent;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String slug;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String name;

    @OneToMany
    @JoinColumn(name = "parent_id")
    private List<GenreEntity> childGenres;

    @ManyToMany
    @JoinTable(name = "book2genre",
            joinColumns = @JoinColumn(name = "genre_id"),
            inverseJoinColumns = @JoinColumn(name = "book_id"))
    private List<BookEntity> books;
}

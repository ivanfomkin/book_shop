package com.github.ivanfomkin.bookshop.entity.book.links;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "book2tag")
public class Book2TagEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "book_id")
    private Integer bookId;

    @Column(name = "tag_id")
    private Integer tagId;
}

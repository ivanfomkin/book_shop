package com.github.ivanfomkin.bookshop.entity.book.file;

import com.github.ivanfomkin.bookshop.entity.enums.BookFiletype;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "book_file_type")
public class BookFileTypeEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private BookFiletype name;

    @Column(columnDefinition = "TEXT")
    private String description;
}

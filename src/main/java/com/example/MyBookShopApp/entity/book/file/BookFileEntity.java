package com.example.MyBookShopApp.entity.book.file;

import com.example.MyBookShopApp.entity.book.BookEntity;
import com.example.MyBookShopApp.entity.enums.BookFiletype;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "book_file")
public class BookFileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String hash;

    @Column(columnDefinition = "INT NOT NULL")
    private Integer typeId;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String path;

    @ManyToOne
    @JoinColumn(name = "book_id", referencedColumnName = "id")
    private BookEntity book;

    public String getBookFileExtensionString() {
        return BookFiletype.PDF.getFileExtensionById(typeId);
    }
}

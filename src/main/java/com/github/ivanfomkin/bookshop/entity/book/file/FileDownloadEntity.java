package com.github.ivanfomkin.bookshop.entity.book.file;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "file_download")
public class FileDownloadEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "INT NOT NULL")
    private Integer userId;

    @Column(columnDefinition = "INT NOT NULL")
    private Integer bookId;

    @Column(columnDefinition = "INT NOT NULL DEFAULT 1")
    private Integer count;

}

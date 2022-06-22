package com.github.ivanfomkin.bookshop.entity.book.links;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "book2user")
public class Book2UserEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @UpdateTimestamp
    @Column(columnDefinition = "TIMESTAMP NOT NULL")
    private LocalDateTime time;

    @Column(name = "type_id", columnDefinition = "INT NOT NULL")
    private Integer typeId;

    @Column(name = "book_id", columnDefinition = "INT NOT NULL")
    private Integer bookId;

    @Column(name = "user_id", columnDefinition = "INT NOT NULL")
    private Integer userId;

}

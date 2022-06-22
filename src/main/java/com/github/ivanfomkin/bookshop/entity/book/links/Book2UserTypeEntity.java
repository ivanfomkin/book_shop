package com.github.ivanfomkin.bookshop.entity.book.links;

import com.github.ivanfomkin.bookshop.entity.enums.Book2UserType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "book2user_type")
public class Book2UserTypeEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private Book2UserType name;

}

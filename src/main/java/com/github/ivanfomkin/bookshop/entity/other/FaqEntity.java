package com.github.ivanfomkin.bookshop.entity.other;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "faq")
public class FaqEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "INT NOT NULL  DEFAULT 0")
    private Integer sortIndex;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String question;

    @Column(columnDefinition = "TEXT NOT NULL")
    private String answer;

}

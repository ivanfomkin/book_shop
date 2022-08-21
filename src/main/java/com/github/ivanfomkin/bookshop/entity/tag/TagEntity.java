package com.github.ivanfomkin.bookshop.entity.tag;

import com.github.ivanfomkin.bookshop.entity.book.BookEntity;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
@Builder
@Getter
@Setter
@Entity
@Table(name = "tags")
@NoArgsConstructor
@AllArgsConstructor
public class TagEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    private String name;

    @ManyToMany
    @JoinTable(name = "book2tag",
            joinColumns = @JoinColumn(name = "tag_id"),
            inverseJoinColumns = @JoinColumn(name = "book_id"))
    private List<BookEntity> books;
}

package com.github.ivanfomkin.bookshop.entity.book.bookview;

import com.github.ivanfomkin.bookshop.entity.book.BookEntity;
import com.github.ivanfomkin.bookshop.entity.user.UserEntity;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@Entity
@Table(name = "book_view_history")
@AllArgsConstructor
@NoArgsConstructor
public class BookViewHistoryEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private BookEntity book;
    @ManyToOne
    private UserEntity user;

    @CreationTimestamp
    private LocalDateTime viewDate;
}

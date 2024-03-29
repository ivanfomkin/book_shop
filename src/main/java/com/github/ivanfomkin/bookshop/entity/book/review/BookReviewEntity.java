package com.github.ivanfomkin.bookshop.entity.book.review;

import com.github.ivanfomkin.bookshop.entity.book.BookEntity;
import com.github.ivanfomkin.bookshop.entity.user.UserEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "book_review")
public class BookReviewEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @UpdateTimestamp
    @Column(columnDefinition = "TIMESTAMP NOT NULL")
    private LocalDateTime time;

    @Column(columnDefinition = "TEXT NOT NULL")
    private String text;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private BookEntity book;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @OneToMany(mappedBy = "bookReview", cascade = CascadeType.REMOVE)
    private List<BookReviewLikeEntity> reviewLikes;

    @Formula("(SELECT count(l.id) FROM book_review_like l WHERE l.value = 1 AND l.review_id = id)")
    private int likeCount;

    @Formula("(SELECT count(l.id) FROM book_review_like l WHERE l.value = -1 AND l.review_id = id)")
    private int dislikeCount;
}

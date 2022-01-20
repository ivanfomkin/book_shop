package com.example.MyBookShopApp.entity.book.review;

import com.example.MyBookShopApp.entity.user.UserEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "book_review_like")
public class BookReviewLikeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @UpdateTimestamp
    @Column(columnDefinition = "TIMESTAMP NOT NULL")
    private LocalDateTime time;

    @Column(columnDefinition = "SMALLINT NOT NULL")
    private Short value;

    @ManyToOne
    @JoinColumn(name = "review_id")
    private BookReviewEntity bookReview;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
}

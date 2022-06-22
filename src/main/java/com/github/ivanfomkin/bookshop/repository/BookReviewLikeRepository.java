package com.github.ivanfomkin.bookshop.repository;

import com.github.ivanfomkin.bookshop.entity.book.review.BookReviewEntity;
import com.github.ivanfomkin.bookshop.entity.book.review.BookReviewLikeEntity;
import com.github.ivanfomkin.bookshop.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookReviewLikeRepository extends JpaRepository<BookReviewLikeEntity, Integer> {
    BookReviewLikeEntity findBookReviewLikeEntityByBookReviewAndUser(BookReviewEntity review, UserEntity user);
}

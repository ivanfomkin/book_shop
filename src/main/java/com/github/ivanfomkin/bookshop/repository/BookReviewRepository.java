package com.github.ivanfomkin.bookshop.repository;

import com.github.ivanfomkin.bookshop.entity.book.BookEntity;
import com.github.ivanfomkin.bookshop.entity.book.review.BookReviewEntity;
import com.github.ivanfomkin.bookshop.entity.user.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookReviewRepository extends JpaRepository<BookReviewEntity, Integer> {
    List<BookReviewEntity> findBookReviewEntitiesByBook(BookEntity book);

    boolean existsBookReviewEntityByBookAndUser(BookEntity book, UserEntity entity);

    @Query("SELECT bre FROM BookReviewEntity bre WHERE bre.text LIKE CONCAT('%',:searchQuery,'%') OR bre.book.title LIKE CONCAT('%',:searchQuery,'%') OR bre.user.name LIKE CONCAT('%',:searchQuery,'%')")
    Page<BookReviewEntity> findAllBySearchQuery(Pageable pageable, String searchQuery);

}

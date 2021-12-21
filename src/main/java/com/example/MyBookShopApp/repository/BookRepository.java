package com.example.MyBookShopApp.repository;

import com.example.MyBookShopApp.entity.book.BookEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<BookEntity, Integer> {
    @Query("SELECT b FROM BookEntity b LEFT OUTER JOIN BookReviewEntity br ON br.bookId = b.id " +
            "LEFT OUTER JOIN BookReviewLikeEntity bl ON bl.reviewId = br.id " +
            "GROUP BY b.id ORDER BY -AVG(bl.value), b.publishDate DESC")
    Page<BookEntity> finRecommendedBooks(Pageable pageable);
}

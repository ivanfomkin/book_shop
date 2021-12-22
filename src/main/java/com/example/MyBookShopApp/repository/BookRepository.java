package com.example.MyBookShopApp.repository;

import com.example.MyBookShopApp.entity.book.BookEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface BookRepository extends JpaRepository<BookEntity, Integer> {
    @Query("""
            SELECT b FROM BookEntity b LEFT OUTER JOIN BookReviewEntity br ON br.bookId = b.id
            LEFT OUTER JOIN BookReviewLikeEntity bl ON bl.reviewId = br.id
            GROUP BY b.id ORDER BY -AVG(bl.value), b.publishDate DESC
            """)
    Page<BookEntity> finRecommendedBooks(Pageable pageable);

    @Query("SELECT b FROM BookEntity b WHERE b.publishDate <= CURRENT_DATE ORDER BY b.publishDate")
    Page<BookEntity> findRecentBooks(Pageable pageable);

    Page<BookEntity> findBookEntitiesByPublishDateBetweenOrderByPublishDateDesc(LocalDate fromDate, LocalDate toDate, Pageable pageable);

    @Query("""
            SELECT b FROM BookEntity b LEFT OUTER JOIN Book2UserEntity b2u ON b2u.bookId = b.id
            LEFT OUTER JOIN Book2UserTypeEntity b2ut ON b2ut.id = b2u.typeId
            GROUP BY b.id
            ORDER BY SUM(CASE WHEN b2ut.name = 'PAID' THEN 1 WHEN (b2ut.name = 'CART') THEN 0.7 WHEN (b2ut.name = 'KEPT') THEN 0.4 ELSE 0 END) DESC
            """)
    Page<BookEntity> findPopularBooks(Pageable pageable);
}

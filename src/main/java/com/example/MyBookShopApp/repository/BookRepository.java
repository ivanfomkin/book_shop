package com.example.MyBookShopApp.repository;

import com.example.MyBookShopApp.entity.author.AuthorEntity;
import com.example.MyBookShopApp.entity.book.BookEntity;
import com.example.MyBookShopApp.entity.genre.GenreEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Repository
public interface BookRepository extends JpaRepository<BookEntity, Integer> {
    @Query("""
            SELECT b FROM BookEntity b LEFT OUTER JOIN BookReviewEntity br ON br.bookId = b.id
            LEFT OUTER JOIN BookReviewLikeEntity bl ON bl.reviewId = br.id
            GROUP BY b.id ORDER BY -AVG(bl.value), b.publishDate DESC
            """)
    Page<BookEntity> finRecommendedBooks(Pageable pageable);

    @Query("SELECT b FROM BookEntity b WHERE b.publishDate <= CURRENT_DATE ORDER BY b.publishDate DESC")
    Page<BookEntity> findRecentBooks(Pageable pageable);

    Page<BookEntity> findBookEntitiesByPublishDateBetweenOrderByPublishDateDesc(LocalDate fromDate, LocalDate toDate, Pageable pageable);

    @Query("""
            SELECT b FROM BookEntity b LEFT OUTER JOIN Book2UserEntity b2u ON b2u.bookId = b.id
            LEFT OUTER JOIN Book2UserTypeEntity b2ut ON b2ut.id = b2u.typeId
            GROUP BY b.id
            ORDER BY SUM(CASE WHEN b2ut.name = 'PAID' THEN 1 WHEN (b2ut.name = 'CART') THEN 0.7 WHEN (b2ut.name = 'KEPT') THEN 0.4 ELSE 0 END) DESC, b.publishDate DESC
            """)
    Page<BookEntity> findPopularBooks(Pageable pageable);

    @Query("SELECT b FROM BookEntity b JOIN Book2TagEntity b2t ON b2t.bookId = b.id " +
            "JOIN TagEntity t ON t.id = b2t.tagId WHERE t.name = :tagName")
    Page<BookEntity> findBookEntityByTagName(String tagName, Pageable pageable);

    Page<BookEntity> findBookEntityByTitleContainingOrderByTitle(String searchRequest, Pageable pageable);

    Page<BookEntity> findBookEntityByGenresContaining(GenreEntity genre, Pageable pageable);

    Page<BookEntity> findBookEntityByAuthorsContaining(AuthorEntity author, Pageable pageable);

    @Query("""
            SELECT b FROM BookEntity b JOIN Book2AuthorEntity b2a ON b.id = b2a.bookId
            JOIN AuthorEntity a ON a.id = b2a.authorId WHERE a.slug = :authorSlug
            """)
    Page<BookEntity> findBookEntityByAuthorsSlug(String authorSlug, Pageable pageable);

    BookEntity findBookEntityBySlug(String slug);

    @Modifying
    @Transactional
    @Query("UPDATE BookEntity SET image = :image WHERE slug = :slug")
    void updateBookImageBySlug(String slug, @Param("image") String imagePath);
}

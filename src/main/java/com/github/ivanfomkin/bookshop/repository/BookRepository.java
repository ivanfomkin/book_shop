package com.github.ivanfomkin.bookshop.repository;

import com.github.ivanfomkin.bookshop.aop.annotation.ExecutionTimeLog;
import com.github.ivanfomkin.bookshop.entity.author.AuthorEntity;
import com.github.ivanfomkin.bookshop.entity.book.BookEntity;
import com.github.ivanfomkin.bookshop.entity.enums.Book2UserType;
import com.github.ivanfomkin.bookshop.entity.genre.GenreEntity;
import com.github.ivanfomkin.bookshop.entity.user.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<BookEntity, Integer> {
    @Query("""
            SELECT b FROM BookEntity b LEFT OUTER JOIN b.votes v WHERE b.slug NOT IN :slugs
            GROUP BY b.id ORDER BY AVG(v.value) DESC, count(v) DESC, b.publishDate DESC
            """)
    Page<BookEntity> findRecommendedBooksWhereSlugsNotIn(Pageable pageable, List<String> slugs);

    @Query("SELECT b FROM BookEntity b WHERE b.publishDate <= CURRENT_DATE ORDER BY b.publishDate DESC")
    Page<BookEntity> findRecentBooks(Pageable pageable);

    Page<BookEntity> findBookEntitiesByPublishDateBetweenOrderByPublishDateDesc(LocalDate fromDate, LocalDate toDate, Pageable pageable);

    @ExecutionTimeLog
    @Query("""
            SELECT b FROM BookEntity b LEFT OUTER JOIN Book2UserEntity b2u ON b2u.book = b
            LEFT OUTER JOIN b2u.type b2ut ON b2ut.id = b2u.type.id
            GROUP BY b.id
            ORDER BY SUM(CASE WHEN b2ut.name = 'PAID' THEN 1 WHEN (b2ut.name = 'CART') THEN 0.7 WHEN (b2ut.name = 'KEPT') THEN 0.4 ELSE 0 END) DESC, b.publishDate DESC
            """)
    Page<BookEntity> findPopularBooks(Pageable pageable);

    @Query("SELECT b FROM BookEntity b JOIN Book2TagEntity b2t ON b2t.book = b " +
            "JOIN TagEntity t ON t = b2t.tag WHERE t.name = :tagName")
    Page<BookEntity> findBookEntityByTagName(String tagName, Pageable pageable);

    Page<BookEntity> findBookEntityByTitleContainingIgnoreCaseOrderByTitle(String searchRequest, Pageable pageable);

    Page<BookEntity> findBookEntityByGenresContaining(GenreEntity genre, Pageable pageable);

    Page<BookEntity> findBookEntityByAuthorsContaining(AuthorEntity author, Pageable pageable);

    @Query("""
            SELECT b FROM BookEntity b JOIN Book2AuthorEntity b2a ON b = b2a.book
            JOIN AuthorEntity a ON a = b2a.author WHERE a.slug = :authorSlug
            """)
    Page<BookEntity> findBookEntityByAuthorsSlug(String authorSlug, Pageable pageable);

    Optional<BookEntity> findBookEntityBySlug(String slug);

    @Modifying
    @Transactional
    @Query("UPDATE BookEntity SET image = :image WHERE slug = :slug")
    void updateBookImageBySlug(String slug, @Param("image") String imagePath);

    @Query("SELECT b2u.book FROM Book2UserEntity b2u WHERE b2u.type.name = :type AND b2u.user = :user")
    List<BookEntity> findBookEntitiesByUserAndType(UserEntity user, Book2UserType type);

    @Query("SELECT id FROM BookEntity where slug = :slug")
    Integer findBookIdBySlug(String slug);

    List<BookEntity> findBookEntitiesBySlugIn(List<String> slugList);

    @Query("""
            SELECT b FROM BookEntity b LEFT OUTER JOIN b.votes v
            WHERE b NOT IN (SELECT b2u.book FROM Book2UserEntity b2u JOIN UserEntity u ON u = b2u.user WHERE u = :currentUser)
            GROUP BY b.id ORDER BY AVG(v.value) DESC, count(v) DESC, b.publishDate DESC
            """)
    Page<BookEntity> findRecommendedBooksForUser(Pageable pageable, UserEntity currentUser);

    Page<BookEntity> findBookEntitiesByTitleContainingIgnoreCase(Pageable pageable, String searchQuery);

    @Modifying
    @Transactional
    void deleteBookEntityBySlug(String slug);

    @Query("SELECT b FROM BookEntity b WHERE b NOT IN (SELECT b2u.book FROM Book2UserEntity b2u WHERE b2u.user.id = :userId AND (b2u.type.name = com.github.ivanfomkin.bookshop.entity.enums.Book2UserType.ARCHIVED OR b2u.type.name = com.github.ivanfomkin.bookshop.entity.enums.Book2UserType.PAID))")
    List<BookEntity> findBooksForGiftByUserId(Integer userId);

    Page<BookEntity> findBookEntityByIdIn(Pageable pageable, List<Integer> ids);

    @Query("SELECT COUNT(b) FROM BookEntity b")
    int countAll();
}

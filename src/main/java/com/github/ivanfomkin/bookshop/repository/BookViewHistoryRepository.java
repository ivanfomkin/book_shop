package com.github.ivanfomkin.bookshop.repository;

import com.github.ivanfomkin.bookshop.entity.book.BookEntity;
import com.github.ivanfomkin.bookshop.entity.book.bookview.BookViewHistoryEntity;
import com.github.ivanfomkin.bookshop.entity.book.bookview.BookViewHistoryId;
import com.github.ivanfomkin.bookshop.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookViewHistoryRepository extends JpaRepository<BookViewHistoryEntity, BookViewHistoryId> {

    @Query("""
            SELECT bvh.bookViewHistoryId.book FROM BookViewHistoryEntity bvh 
            WHERE bvh.bookViewHistoryId.user = :user 
            AND bvh.lastViewDate BETWEEN :minDate AND CURRENT_TIMESTAMP 
            ORDER BY bvh.lastViewDate DESC""")
    List<BookEntity> findRecentViewedBooksByUserAndViewDateBetween(UserEntity user, LocalDateTime minDate);
}

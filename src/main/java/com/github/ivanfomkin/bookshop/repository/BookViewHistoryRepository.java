package com.github.ivanfomkin.bookshop.repository;

import com.github.ivanfomkin.bookshop.entity.book.BookEntity;
import com.github.ivanfomkin.bookshop.entity.book.bookview.BookViewHistoryEntity;
import com.github.ivanfomkin.bookshop.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookViewHistoryRepository extends JpaRepository<BookViewHistoryEntity, Long> {
    @Query("SELECT DISTINCT bvh.book FROM BookViewHistoryEntity bvh WHERE bvh.user = :user and bvh.viewDate BETWEEN :minDate and CURRENT_TIMESTAMP")
    List<BookEntity> findBookEntitiesByUserAndViewDateAfter(UserEntity user, LocalDateTime minDate);
}

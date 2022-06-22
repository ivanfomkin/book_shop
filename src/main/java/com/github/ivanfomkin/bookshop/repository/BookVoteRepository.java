package com.github.ivanfomkin.bookshop.repository;

import com.github.ivanfomkin.bookshop.entity.book.BookEntity;
import com.github.ivanfomkin.bookshop.entity.book.review.BookVoteEntity;
import com.github.ivanfomkin.bookshop.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookVoteRepository extends JpaRepository<BookVoteEntity, Integer> {

    @Query("SELECT avg(bv.value) FROM BookVoteEntity bv WHERE bv.book = :book")
    Float getBookRating(BookEntity book);

    BookVoteEntity findBookVoteEntityByUserAndBook(UserEntity user, BookEntity book);

    List<BookVoteEntity> findBookVoteEntitiesByBook(BookEntity book);
}

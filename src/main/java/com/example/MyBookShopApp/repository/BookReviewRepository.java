package com.example.MyBookShopApp.repository;

import com.example.MyBookShopApp.entity.book.BookEntity;
import com.example.MyBookShopApp.entity.book.review.BookReviewEntity;
import com.example.MyBookShopApp.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookReviewRepository extends JpaRepository<BookReviewEntity, Integer> {
    List<BookReviewEntity> findBookReviewEntitiesByBook(BookEntity book);

    Boolean existsBookReviewEntityByBookAndUser(BookEntity book, UserEntity entity);
}

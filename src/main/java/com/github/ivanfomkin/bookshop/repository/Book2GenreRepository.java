package com.github.ivanfomkin.bookshop.repository;

import com.github.ivanfomkin.bookshop.entity.book.BookEntity;
import com.github.ivanfomkin.bookshop.entity.book.links.Book2GenreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Book2GenreRepository extends JpaRepository<Book2GenreEntity, Integer> {
    List<Book2GenreEntity> findAllByBook(BookEntity book);
}

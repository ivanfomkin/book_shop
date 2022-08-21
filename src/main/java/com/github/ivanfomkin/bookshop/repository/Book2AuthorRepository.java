package com.github.ivanfomkin.bookshop.repository;

import com.github.ivanfomkin.bookshop.entity.book.BookEntity;
import com.github.ivanfomkin.bookshop.entity.book.links.Book2AuthorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Book2AuthorRepository extends JpaRepository<Book2AuthorEntity, Integer> {
    List<Book2AuthorEntity> findAllByBook(BookEntity book);
}

package com.github.ivanfomkin.bookshop.repository;

import com.github.ivanfomkin.bookshop.entity.book.BookEntity;
import com.github.ivanfomkin.bookshop.entity.book.file.BookFileEntity;
import com.github.ivanfomkin.bookshop.entity.enums.BookFiletype;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BookFileRepository extends JpaRepository<BookFileEntity, Integer> {
    @Cacheable(key = "#hash", cacheNames = "booksByHash")
    BookFileEntity findBookFileEntityByHash(String hash);

    @Query("SELECT b FROM BookFileEntity b JOIN BookFileTypeEntity bf ON b.typeId = bf.id WHERE b.book = :book AND bf.name = :filetype")
    BookFileEntity findBookFileEntityByBookAndType(BookEntity book, BookFiletype filetype);
}

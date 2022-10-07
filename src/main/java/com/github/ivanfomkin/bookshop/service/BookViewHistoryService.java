package com.github.ivanfomkin.bookshop.service;

import com.github.ivanfomkin.bookshop.entity.book.BookEntity;
import com.github.ivanfomkin.bookshop.entity.user.UserEntity;

import java.util.List;

public interface BookViewHistoryService {
    void saveBookView(UserEntity user, BookEntity book);

    List<BookEntity> findRecentBooksViewByUser(UserEntity currentUser);
}

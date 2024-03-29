package com.github.ivanfomkin.bookshop.service.impl;

import com.github.ivanfomkin.bookshop.entity.book.BookEntity;
import com.github.ivanfomkin.bookshop.entity.book.bookview.BookViewHistoryEntity;
import com.github.ivanfomkin.bookshop.entity.user.UserEntity;
import com.github.ivanfomkin.bookshop.repository.BookViewHistoryRepository;
import com.github.ivanfomkin.bookshop.service.BookViewHistoryService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookViewHistoryServiceImpl implements BookViewHistoryService {
    private final BookViewHistoryRepository bookViewHistoryRepository;

    @Value("${bookshop.books.recent.days}")
    private long recentBookDays;

    public BookViewHistoryServiceImpl(BookViewHistoryRepository bookViewHistoryRepository) {
        this.bookViewHistoryRepository = bookViewHistoryRepository;
    }

    @Override
    public void saveBookView(UserEntity user, BookEntity book) {
        bookViewHistoryRepository.save(BookViewHistoryEntity
                .builder()
                .book(book)
                .user(user)
                .build());
    }

    @Override
    public List<BookEntity> findRecentBooksViewByUser(UserEntity currentUser) {
        var minBookViewDate = LocalDateTime.now().minusDays(recentBookDays);
        return bookViewHistoryRepository.findBookEntitiesByUserAndViewDateAfter(currentUser, minBookViewDate);
    }
}

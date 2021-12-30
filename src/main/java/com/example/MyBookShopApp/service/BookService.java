package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.dto.book.BookListDto;
import com.example.MyBookShopApp.entity.genre.GenreEntity;

public interface BookService {
    BookListDto getPageableRecommendedBooks(int offset, int limit);

    BookListDto getPageableRecentBooks(int offset, int limit);

    BookListDto getPageablePopularBooks(int offset, int limit);

    BookListDto getPageableRecentBooks(int offset, int limit, String fromDate, String toDate);

    BookListDto getPageableBooksByTag(int offset, int limit, String tag);

    BookListDto getPageableBooksByTitle(int offset, int limit, String title);

    BookListDto getPageableBooksByGenre(int offset, int limit, GenreEntity genre);
}

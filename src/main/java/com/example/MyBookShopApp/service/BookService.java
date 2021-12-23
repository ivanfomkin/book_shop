package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.dto.book.BookListDto;

public interface BookService {
    BookListDto getPageableRecommendedBooks(Integer offset, Integer limit);

    BookListDto getPageableRecentBooks(Integer offset, Integer limit);

    BookListDto getPageablePopularBooks(int offset, int limit);

    BookListDto getPageableRecentBooks(Integer offset, Integer limit, String fromDate, String toDate);

    BookListDto getPageableBooksByTag(Integer offset, Integer limit, String tag);
}

package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.dto.book.BookListDto;
import com.example.MyBookShopApp.entity.author.AuthorEntity;
import com.example.MyBookShopApp.entity.book.BookEntity;
import com.example.MyBookShopApp.entity.genre.GenreEntity;

import java.util.List;

public interface BookService {
    BookListDto getPageableRecommendedBooks(int offset, int limit);

    BookListDto getPageableRecentBooks(int offset, int limit);

    BookListDto getPageablePopularBooks(int offset, int limit);

    BookListDto getPageableRecentBooks(int offset, int limit, String fromDate, String toDate);

    BookListDto getPageableBooksByTag(int offset, int limit, String tag);

    BookListDto getPageableBooksByTitle(int offset, int limit, String title);

    BookListDto getPageableBooksByGenre(int offset, int limit, GenreEntity genre);

    BookListDto getPageableBooksByAuthor(int offset, int limit, AuthorEntity author);

    BookListDto getPageableBooksByAuthorSlug(int offset, int limit, String authorSlug);

    BookEntity getBookBySlug(String slug);

    void updateBookImageBySlug(String slug, String imagePath);

    List<BookEntity> getBooksBySlugIn(String[] cookieSlug);

    int calculateBookDiscountPrice(int price, Short percentDiscount);
}

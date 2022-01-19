package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.dto.book.BookListDto;
import com.example.MyBookShopApp.dto.book.BookSlugDto;
import com.example.MyBookShopApp.entity.author.AuthorEntity;
import com.example.MyBookShopApp.entity.book.BookEntity;
import com.example.MyBookShopApp.entity.enums.Book2UserType;
import com.example.MyBookShopApp.entity.genre.GenreEntity;
import com.example.MyBookShopApp.entity.user.UserEntity;

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

    BookSlugDto getBookSlugDtoBySlug(String slug);

    BookEntity getBookEntityBySlug(String slug);

    void updateBookImageBySlug(String slug, String imagePath);

    int calculateBookDiscountPrice(int price, Short percentDiscount);

    List<BookEntity> getBooksByUserAndType(UserEntity user, Book2UserType cart);

    Integer getBookIdBuSlug(String slug);
}

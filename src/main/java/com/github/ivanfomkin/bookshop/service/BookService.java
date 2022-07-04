package com.github.ivanfomkin.bookshop.service;

import com.github.ivanfomkin.bookshop.dto.book.BookListDto;
import com.github.ivanfomkin.bookshop.dto.book.BookSlugDto;
import com.github.ivanfomkin.bookshop.entity.author.AuthorEntity;
import com.github.ivanfomkin.bookshop.entity.book.BookEntity;
import com.github.ivanfomkin.bookshop.entity.enums.Book2UserType;
import com.github.ivanfomkin.bookshop.entity.genre.GenreEntity;
import com.github.ivanfomkin.bookshop.entity.user.UserEntity;

import java.util.List;

public interface BookService {
    BookListDto getPageableRecommendedBooks(int offset, int limit, String cartCookie, String keptCookie);

    BookListDto getPageablePopularBooks(int offset, int limit, String cartCookie, String keptCookie);

    BookListDto getPageableRecentBooks(int offset, int limit, String fromDate, String toDate, String cartCookie, String keptCookie);

    BookListDto getPageableBooksByTag(int offset, int limit, String tag, String cartCookie, String keptCookie);

    BookListDto getPageableBooksByTitle(int offset, int limit, String title, String cartCookie, String keptCookie);

    BookListDto getPageableBooksByGenre(int offset, int limit, GenreEntity genre, String cartCookie, String keptCookie);

    BookListDto getPageableBooksByAuthor(int offset, int limit, AuthorEntity author, String cartCookie, String keptCookie);

    BookListDto getPageableBooksByAuthorSlug(int offset, int limit, String authorSlug, String cartCookie, String keptCookie);

    BookSlugDto getBookSlugDtoBySlug(UserEntity userDto, String slug, String cartCookie, String keptCookie);

    BookEntity getBookEntityBySlug(String slug);

    void updateBookImageBySlug(String slug, String imagePath);

    int calculateBookDiscountPrice(int price, Short percentDiscount);

    List<BookEntity> getBooksByUserAndType(UserEntity user, Book2UserType cart);

    Integer getBookIdBuSlug(String slug);

    List<BookEntity> getBooksBySlugIn(List<String> slugs);

    BookListDto getPageableRecentBooks(String cartCookie, String keptCookie, int offset, int limit);
}

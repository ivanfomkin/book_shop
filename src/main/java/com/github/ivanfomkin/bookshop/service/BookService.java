package com.github.ivanfomkin.bookshop.service;

import com.github.ivanfomkin.bookshop.dto.CommonPageableDto;
import com.github.ivanfomkin.bookshop.dto.book.BookEditDto;
import com.github.ivanfomkin.bookshop.dto.book.BookGiftDto;
import com.github.ivanfomkin.bookshop.dto.book.BookListElement;
import com.github.ivanfomkin.bookshop.dto.book.BookSlugDto;
import com.github.ivanfomkin.bookshop.entity.author.AuthorEntity;
import com.github.ivanfomkin.bookshop.entity.book.BookEntity;
import com.github.ivanfomkin.bookshop.entity.enums.Book2UserType;
import com.github.ivanfomkin.bookshop.entity.genre.GenreEntity;
import com.github.ivanfomkin.bookshop.entity.user.UserEntity;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;

public interface BookService {
    CommonPageableDto<BookListElement> getPageableRecommendedBooks(int offset, int limit, String cartCookie, String keptCookie);

    CommonPageableDto<BookListElement> getPageableAllBooks(Pageable pageable, String searchQuery);

    CommonPageableDto<BookListElement> getPageablePopularBooks(int offset, int limit, String cartCookie, String keptCookie);

    CommonPageableDto<BookListElement> getPageableRecentBooks(int offset, int limit, String fromDate, String toDate, String cartCookie, String keptCookie);

    CommonPageableDto<BookListElement> getPageableBooksByTag(int offset, int limit, String tag, String cartCookie, String keptCookie);

    CommonPageableDto<BookListElement> getPageableBooksByTitle(int offset, int limit, String title, String cartCookie, String keptCookie);

    CommonPageableDto<BookListElement> getPageableBooksByGenre(int offset, int limit, GenreEntity genre, String cartCookie, String keptCookie);

    CommonPageableDto<BookListElement> getPageableBooksByAuthor(int offset, int limit, AuthorEntity author, String cartCookie, String keptCookie);

    CommonPageableDto<BookListElement> getPageableBooksByAuthorSlug(int offset, int limit, String authorSlug, String cartCookie, String keptCookie);

    BookSlugDto getBookSlugDtoBySlug(UserEntity userDto, String slug, String cartCookie, String keptCookie);

    BookEntity getBookEntityBySlug(String slug);

    void updateBookImageBySlug(String slug, String imagePath);

    int calculateBookDiscountPrice(int price, Short percentDiscount);

    List<BookEntity> getBooksByUserAndType(UserEntity user, Book2UserType cart);

    List<BookEntity> getBooksBySlugIn(List<String> slugs);

    CommonPageableDto<BookListElement> getPageableRecentBooks(String cartCookie, String keptCookie, int offset, int limit);

    CommonPageableDto<BookListElement> getPaidBooksByCurrentUser();

    CommonPageableDto<BookListElement> getArchivedBooksByCurrentUser();

    BookEditDto getBookEditDtoBySlug(String slug);

    void updateBookEntity(BookEditDto book) throws IOException;

    void createBook(BookEditDto bookEditDto) throws IOException;

    void deleteBookBySlug(String slug);

    List<BookGiftDto> getBooksForUserGift(Integer userId);
}

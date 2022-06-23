package com.github.ivanfomkin.bookshop.repository;

import com.github.ivanfomkin.bookshop.AbstractTest;
import com.github.ivanfomkin.bookshop.entity.book.BookEntity;
import com.github.ivanfomkin.bookshop.entity.enums.Book2UserType;
import com.github.ivanfomkin.bookshop.entity.tag.TagEntity;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class BookRepositoryTest extends AbstractTest {

    private final JdbcTemplate jdbcTemplate;
    private final BookRepository bookRepository;

    @Autowired
    BookRepositoryTest(JdbcTemplate jdbcTemplate, BookRepository bookRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.bookRepository = bookRepository;
    }

    @Test
    void findBookEntityByTitleContainingIgnoreCaseOrderByTitle_findExistsBooksByTitle_inResultSetOnlyBooksWithMatchesTitle() {
        var searchRequest = "sword";
        var books = bookRepository.findBookEntityByTitleContainingIgnoreCaseOrderByTitle(searchRequest, Pageable.ofSize(Integer.MAX_VALUE));
        assertNotNull(books);
        assertFalse(books.isEmpty());
        for (BookEntity book : books) {
            assertTrue(StringUtils.containsIgnoreCase(book.getTitle(), searchRequest));
        }
    }

    @Test
    void findBookEntityByTitleContainingIgnoreCaseOrderByTitle_findNonExistsBooksByTitle_resultIsNotNullAndEmpty() {
        var searchRequest = "asdhkjklsadbkjsandsjkankl";
        var books = bookRepository.findBookEntityByTitleContainingIgnoreCaseOrderByTitle(searchRequest, Pageable.ofSize(Integer.MAX_VALUE));
        assertNotNull(books);
        assertTrue(books.isEmpty());
    }

    @Test
    void findBookEntityByTagName_searchBookByExistingTag_inResultSetBookWithWantedTag() {
        var tag = "varius";
        var booksByTag = bookRepository.findBookEntityByTagName(tag, Pageable.ofSize(Integer.MAX_VALUE));
        assertNotNull(booksByTag);
        assertFalse(booksByTag.isEmpty());

        for (BookEntity bookEntity : booksByTag) {
            var bookTags = bookEntity.getTags().stream().map(TagEntity::getName).toList();
            assertTrue(bookTags.contains(tag));
        }
    }

    @Test
    void findBookEntityByTagName_searchBookByNonExistingTag_resultIsNotNullAndEmpty() {
        var tag = "112jsandklasndlk";
        var booksByTag = bookRepository.findBookEntityByTagName(tag, Pageable.ofSize(Integer.MAX_VALUE));
        assertNotNull(booksByTag);
        assertTrue(booksByTag.isEmpty());
    }

    @Test
    void finRecommendedBooks_searchAllRecommendedBooks_resultIsNotNullAndNotEmptyAndBookPageSizeIs10() {
        var pageSize = 10;
        var recommendedBooks = bookRepository.finRecommendedBooks(Pageable.ofSize(pageSize));
        assertNotNull(recommendedBooks);
        assertFalse(recommendedBooks.isEmpty());
        assertThat(recommendedBooks.getSize()).isEqualTo(pageSize);
    }

    @Test
    void findRecentBooks_searchAllRecentBooks_resultIsNotNullAndNotEmptyAndBooksSortedByDateDesc() {
        var pageSize = 10;
        var recentBooks = bookRepository.findRecentBooks(Pageable.ofSize(pageSize));
        assertNotNull(recentBooks);
        assertFalse(recentBooks.isEmpty());
        assertThat(recentBooks.getSize()).isEqualTo(pageSize);
        LocalDate lastBookDate = recentBooks.get().findFirst().get().getPublishDate();
        for (BookEntity recentBook : recentBooks) {
            assertTrue(recentBook.getPublishDate().isBefore(lastBookDate) || recentBook.getPublishDate().isEqual(lastBookDate));
            lastBookDate = recentBook.getPublishDate();
        }
    }

    @Test
    void findPopularBooks_find10popularBooks_resultIsNotNullAndNotEmptyAndContain10Books() {
        var pageSize = 10;
        var popularBooks = bookRepository.findPopularBooks(Pageable.ofSize(pageSize));
        assertNotNull(popularBooks);
        assertFalse(popularBooks.isEmpty());
        assertThat(popularBooks.getSize()).isEqualTo(pageSize);
    }

    @Test
    void findPopularBooks_find10popularBooks_booksOrderByPopularRating() {
        var pageSize = 10;
        var popularBooks = bookRepository.findPopularBooks(Pageable.ofSize(pageSize));
        var lastBookRating = calculateBookRating(popularBooks.stream().findFirst().get());
        for (BookEntity popularBook : popularBooks) {
            assertThat(calculateBookRating(popularBook)).isLessThanOrEqualTo(lastBookRating);
            lastBookRating = calculateBookRating(popularBook);
        }
    }

    private double calculateBookRating(BookEntity bookEntity) {
        var usersCountWhereByThisBook = getBookStatusCount(bookEntity, Book2UserType.PAID);
        var usersCountWhereAddThisBookToCart = getBookStatusCount(bookEntity, Book2UserType.CART);
        var usersCountWhereAddThisBookToPostponed = getBookStatusCount(bookEntity, Book2UserType.KEPT);
        return usersCountWhereByThisBook + 0.7 * usersCountWhereAddThisBookToCart + 0.4 * usersCountWhereAddThisBookToPostponed;
    }

    private Integer getBookStatusCount(BookEntity bookEntity, Book2UserType book2UserType) {
        return jdbcTemplate.queryForObject("""
                SELECT count(*)
                FROM book2user b2u
                         join book2user_type b2ut on b2u.type_id = b2ut.id AND b2ut.code = ?
                WHERE book_id = ?
                """, Integer.TYPE, book2UserType.toString(), bookEntity.getId());
    }
}
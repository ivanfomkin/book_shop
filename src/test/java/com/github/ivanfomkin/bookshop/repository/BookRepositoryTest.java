package com.github.ivanfomkin.bookshop.repository;

import com.github.ivanfomkin.bookshop.AbstractTest;
import com.github.ivanfomkin.bookshop.entity.book.BookEntity;
import com.github.ivanfomkin.bookshop.entity.tag.TagEntity;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class BookRepositoryTest extends AbstractTest {

    private final BookRepository bookRepository;

    @Autowired
    BookRepositoryTest(BookRepository bookRepository) {
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
}
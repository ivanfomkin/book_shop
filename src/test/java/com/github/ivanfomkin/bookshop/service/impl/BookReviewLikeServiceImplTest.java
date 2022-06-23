package com.github.ivanfomkin.bookshop.service.impl;

import com.github.ivanfomkin.bookshop.AbstractTest;
import com.github.ivanfomkin.bookshop.dto.book.review.BookReviewLikeRequestDto;
import com.github.ivanfomkin.bookshop.entity.book.review.BookReviewEntity;
import com.github.ivanfomkin.bookshop.entity.book.review.BookReviewLikeEntity;
import com.github.ivanfomkin.bookshop.entity.user.UserEntity;
import com.github.ivanfomkin.bookshop.repository.BookReviewLikeRepository;
import com.github.ivanfomkin.bookshop.service.BookReviewService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.random.RandomGenerator;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BookReviewLikeServiceImplTest extends AbstractTest {

    private final BookReviewLikeServiceImpl bookReviewLikeService;

    @MockBean
    private BookReviewService bookReviewService;

    @MockBean
    private BookReviewLikeRepository bookReviewLikeRepository;

    private static UserEntity testUser;
    private static BookReviewEntity testBookReview;
    private static RandomGenerator randomGenerator;

    @BeforeAll
    static void setUp() {
        testUser = new UserEntity();
        testBookReview = new BookReviewEntity();
        randomGenerator = RandomGenerator.getDefault();
    }

    @Autowired
    BookReviewLikeServiceImplTest(BookReviewLikeServiceImpl bookReviewLikeService) {
        this.bookReviewLikeService = bookReviewLikeService;
    }

    public static Stream<Arguments> correctLikeValues() {
        return Stream.of(
                Arguments.of((short) 1),
                Arguments.of((short) -1));
    }

    public static Stream<Arguments> saveBookReviewLike_saveNewLikeForReviewWithUnexpectedLikeValue_throwExceptionAndDoNotSaveLike() {
        return Stream.of(
                Arguments.of((short) randomGenerator.nextInt(2, Short.MAX_VALUE)),
                Arguments.of((short) randomGenerator.nextInt(Short.MIN_VALUE, (short) -2)),
                Arguments.of((short) 0)
        );
    }

    @ParameterizedTest
    @MethodSource("correctLikeValues")
    void saveBookReviewLike_saveNewLikeForReview_likeSaveSuccess(short likeValue) {
        var request = new BookReviewLikeRequestDto();
        request.setReviewId(randomGenerator.nextInt());
        request.setValue(likeValue);
        doReturn(testBookReview).when(bookReviewService).getBookReviewEntityById(request.getReviewId());
        var likeResult = bookReviewLikeService.saveBookReviewLike(request, testUser);
        assertTrue(likeResult);
        verify(bookReviewLikeRepository).save(any(BookReviewLikeEntity.class));
    }

    @ParameterizedTest
    @MethodSource
    void saveBookReviewLike_saveNewLikeForReviewWithUnexpectedLikeValue_throwExceptionAndDoNotSaveLike(short likeValue) {
        var request = new BookReviewLikeRequestDto();
        request.setReviewId(randomGenerator.nextInt());
        request.setValue(likeValue);
        assertThrows(IllegalArgumentException.class, () -> bookReviewLikeService.saveBookReviewLike(request, testUser));
        verify(bookReviewLikeRepository, never()).save(any());
    }


    @ParameterizedTest
    @MethodSource("correctLikeValues")
    void saveBookReviewLike_saveNewLikeWithNullUser_likeSaveResultIsFalseAndDoNotSaveLike(short likeValue) {
        var request = new BookReviewLikeRequestDto();
        request.setReviewId(randomGenerator.nextInt());
        request.setValue(likeValue);
        var saveLikeResult = bookReviewLikeService.saveBookReviewLike(request, null);
        verify(bookReviewLikeRepository, never()).save(any());
        assertFalse(saveLikeResult);
    }
}
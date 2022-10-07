package com.github.ivanfomkin.bookshop.service.impl;

import com.github.ivanfomkin.bookshop.dto.CommonPageableDto;
import com.github.ivanfomkin.bookshop.dto.CommonResultDto;
import com.github.ivanfomkin.bookshop.dto.book.review.BookReviewDto;
import com.github.ivanfomkin.bookshop.dto.book.review.BookReviewListElementDto;
import com.github.ivanfomkin.bookshop.dto.review.BookReviewElementDto;
import com.github.ivanfomkin.bookshop.dto.review.BookSlugInfoDto;
import com.github.ivanfomkin.bookshop.dto.review.ReviewUserInfo;
import com.github.ivanfomkin.bookshop.entity.book.BookEntity;
import com.github.ivanfomkin.bookshop.entity.book.review.BookReviewEntity;
import com.github.ivanfomkin.bookshop.entity.user.UserEntity;
import com.github.ivanfomkin.bookshop.repository.BookReviewRepository;
import com.github.ivanfomkin.bookshop.service.BookReviewService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class BookReviewServiceImpl implements BookReviewService {
    private final BookReviewRepository bookReviewRepository;

    private static final String INVALID_REVIEW_TEXT_LENGTH_MESSAGE = "Количество символов в отзыве должно быть не менее ";
    private static final String REVIEW_ALREADY_EXIST_MESSAGE = "Вы уже оставляли отзыв";

    @Value("${bookshop.review.min-size}")
    private Integer minimalReviewTextLength;

    public BookReviewServiceImpl(BookReviewRepository bookReviewRepository) {
        this.bookReviewRepository = bookReviewRepository;
    }

    @Override
    public BookReviewDto getReviewDtoForBook(BookEntity book) {
        var dto = new BookReviewDto();
        var reviewList = convertBookReviewsEntityToDto(bookReviewRepository.findBookReviewEntitiesByBook(book));
        dto.setReviews(reviewList);
        dto.setCount(reviewList.size());
        dto.setStars((int) Math.round(reviewList.stream().mapToInt(BookReviewListElementDto::getStars).average().orElse(0)));
        return dto;
    }

    @Override
    public BookReviewEntity getBookReviewEntityById(int id) {
        return bookReviewRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public CommonResultDto saveBookReview(BookEntity book, String text, UserEntity user) {
        boolean result = true;
        String errorMessage = null;
        if (bookReviewRepository.existsBookReviewEntityByBookAndUser(book, user)) {
            result = false;
            errorMessage = REVIEW_ALREADY_EXIST_MESSAGE;
        } else if (text.length() < minimalReviewTextLength) {
            result = false;
            errorMessage = INVALID_REVIEW_TEXT_LENGTH_MESSAGE + minimalReviewTextLength;
        } else {
            BookReviewEntity bookReview = new BookReviewEntity();
            bookReview.setBook(book);
            bookReview.setUser(user);
            bookReview.setText(text);
            bookReviewRepository.save(bookReview);
        }
        var resultDto = new CommonResultDto(result);
        if (!result) {
            resultDto.setError(errorMessage);
        }
        return resultDto;
    }

    @Override
    public CommonPageableDto<BookReviewElementDto> getPageableAllReviews(Pageable pageable, String searchQuery) {
        CommonPageableDto<BookReviewElementDto> dto = new CommonPageableDto<>();
        Page<BookReviewEntity> reviewEntities;
        if (searchQuery == null || searchQuery.isBlank()) {
            reviewEntities = bookReviewRepository.findAll(pageable);
        } else {
            reviewEntities = bookReviewRepository.findAllBySearchQuery(pageable, searchQuery);
        }
        dto.setTotal(reviewEntities.getTotalElements());
        dto.setPage(reviewEntities.getNumber());
        dto.setPerPage(pageable.getPageSize());
        var books = reviewEntities.stream().map(e -> {
            var reviewDto = new BookReviewElementDto();
            reviewDto.setId(e.getId());
            reviewDto.setText(e.getText());
            reviewDto.setTime(e.getTime());
            reviewDto.setUser(new ReviewUserInfo(e.getUser().getId(), e.getUser().getName()));
            reviewDto.setBook(new BookSlugInfoDto(e.getBook().getSlug(), e.getBook().getTitle()));
            return reviewDto;
        }).toList();
        dto.setData(books);
        return dto;
    }

    @Override
    @Transactional
    public void deleteReviewById(Integer reviewId) {
        bookReviewRepository.deleteById(reviewId);
    }

    private List<BookReviewListElementDto> convertBookReviewsEntityToDto(List<BookReviewEntity> bookReviewEntities) {
        return bookReviewEntities.stream().map(this::convertBookReviewEntityToBookReviewDto).toList();
    }

    private BookReviewListElementDto convertBookReviewEntityToBookReviewDto(BookReviewEntity entity) {
        var dto = new BookReviewListElementDto();
        dto.setUserName(entity.getUser().getName());
        dto.setDislikes(entity.getDislikeCount());
        dto.setLikes(entity.getLikeCount());
        dto.setReviewDate(entity.getTime());
        dto.setReviewId(entity.getId());
        dto.setText(entity.getText());
        dto.setStars(calculateStarValue(entity.getLikeCount(), entity.getDislikeCount()));
        return dto;
    }

    private short calculateStarValue(int likeCount, int dislikeCount) {
        return (short) (((float) likeCount) / (likeCount + dislikeCount) * 5);
    }
}

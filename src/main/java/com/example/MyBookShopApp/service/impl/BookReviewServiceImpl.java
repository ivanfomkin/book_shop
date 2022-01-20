package com.example.MyBookShopApp.service.impl;

import com.example.MyBookShopApp.dto.book.review.BookReviewDto;
import com.example.MyBookShopApp.dto.book.review.BookReviewListElementDto;
import com.example.MyBookShopApp.entity.book.BookEntity;
import com.example.MyBookShopApp.entity.book.review.BookReviewEntity;
import com.example.MyBookShopApp.entity.user.UserEntity;
import com.example.MyBookShopApp.repository.BookReviewRepository;
import com.example.MyBookShopApp.service.BookReviewService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BookReviewServiceImpl implements BookReviewService {
    private final BookReviewRepository bookReviewRepository;

    private final String INVALID_REVIEW_TEXT_LENGTH_MESSAGE = "Количество символов в отзыве должно быть не менее ";
    private final String REVIEW_ALREADY_EXIST_MESSAGE = "Вы уже оставляли отзыв";

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
    public Map<String, Object> saveBookReview(BookEntity book, String text, UserEntity user) {
        Map<String, Object> resultMap = new HashMap<>();
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
        resultMap.put("result", result);
        if (!result) {
            resultMap.put("error", errorMessage);
        }
        return resultMap;
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

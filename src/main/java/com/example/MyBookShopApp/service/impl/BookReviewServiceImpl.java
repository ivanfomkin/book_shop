package com.example.MyBookShopApp.service.impl;

import com.example.MyBookShopApp.dto.book.review.BookReviewDto;
import com.example.MyBookShopApp.dto.book.review.BookReviewListElementDto;
import com.example.MyBookShopApp.entity.book.BookEntity;
import com.example.MyBookShopApp.entity.book.review.BookReviewEntity;
import com.example.MyBookShopApp.repository.BookReviewRepository;
import com.example.MyBookShopApp.service.BookReviewService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookReviewServiceImpl implements BookReviewService {
    private final BookReviewRepository bookReviewRepository;

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

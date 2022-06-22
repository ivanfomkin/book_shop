package com.github.ivanfomkin.bookshop.service.impl;

import com.github.ivanfomkin.bookshop.dto.book.review.BookReviewLikeRequestDto;
import com.github.ivanfomkin.bookshop.entity.book.review.BookReviewEntity;
import com.github.ivanfomkin.bookshop.entity.book.review.BookReviewLikeEntity;
import com.github.ivanfomkin.bookshop.entity.user.UserEntity;
import com.github.ivanfomkin.bookshop.repository.BookReviewLikeRepository;
import com.github.ivanfomkin.bookshop.service.BookReviewLikeService;
import com.github.ivanfomkin.bookshop.service.BookReviewService;
import org.springframework.stereotype.Service;

@Service
public class BookReviewLikeServiceImpl implements BookReviewLikeService {
    private final BookReviewService bookReviewService;
    private final BookReviewLikeRepository bookReviewLikeRepository;

    public BookReviewLikeServiceImpl(BookReviewService bookReviewService, BookReviewLikeRepository bookReviewLikeRepository) {
        this.bookReviewService = bookReviewService;
        this.bookReviewLikeRepository = bookReviewLikeRepository;
    }

    @Override
    public boolean saveBookReviewLike(BookReviewLikeRequestDto dto, UserEntity userEntity) {
        if (userEntity == null) {
            return false;
        }
        if (Math.abs(dto.getValue()) != 1) {
            throw new IllegalArgumentException("Значение value у лайка должно быть -1 или 1");
        }
        BookReviewEntity bookReview = bookReviewService.getBookReviewEntityById(dto.getReviewId());
        BookReviewLikeEntity like = bookReviewLikeRepository.findBookReviewLikeEntityByBookReviewAndUser(bookReview, userEntity);
        if (like == null) {
            like = new BookReviewLikeEntity();
            like.setBookReview(bookReview);
            like.setUser(userEntity);
            like.setValue(dto.getValue());
            bookReviewLikeRepository.save(like);
        } else {
            like.setValue(dto.getValue());
        }
        return true;
    }
}

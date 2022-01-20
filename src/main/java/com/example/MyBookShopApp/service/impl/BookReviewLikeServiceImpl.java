package com.example.MyBookShopApp.service.impl;

import com.example.MyBookShopApp.dto.book.review.BookReviewLikeRequestDto;
import com.example.MyBookShopApp.entity.book.review.BookReviewEntity;
import com.example.MyBookShopApp.entity.book.review.BookReviewLikeEntity;
import com.example.MyBookShopApp.entity.user.UserEntity;
import com.example.MyBookShopApp.repository.BookReviewLikeRepository;
import com.example.MyBookShopApp.service.BookReviewLikeService;
import com.example.MyBookShopApp.service.BookReviewService;
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
    public void saveBookReviewLike(BookReviewLikeRequestDto dto, UserEntity user) {
        if (Math.abs(dto.getValue()) != 1) {
            throw new IllegalArgumentException("Значение value у лайка должно быть -1 или 1");
        }
        BookReviewEntity bookReview = bookReviewService.getBookReviewEntityById(dto.getReviewId());
        BookReviewLikeEntity like = bookReviewLikeRepository.findBookReviewLikeEntityByBookReviewAndUser(bookReview, user);
        if (like == null) {
            like = new BookReviewLikeEntity();
            like.setBookReview(bookReview);
            like.setUser(user);
            like.setValue(dto.getValue());
            bookReviewLikeRepository.save(like);
        } else {
            like.setValue(dto.getValue());
        }
    }
}

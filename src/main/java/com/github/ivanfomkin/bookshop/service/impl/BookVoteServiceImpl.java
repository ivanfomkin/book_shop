package com.github.ivanfomkin.bookshop.service.impl;

import com.github.ivanfomkin.bookshop.dto.book.BookRatingInfoDto;
import com.github.ivanfomkin.bookshop.entity.book.BookEntity;
import com.github.ivanfomkin.bookshop.entity.book.review.BookVoteEntity;
import com.github.ivanfomkin.bookshop.entity.user.UserEntity;
import com.github.ivanfomkin.bookshop.repository.BookVoteRepository;
import com.github.ivanfomkin.bookshop.service.BookVoteService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BookVoteServiceImpl implements BookVoteService {
    private final BookVoteRepository bookVoteRepository;

    public BookVoteServiceImpl(BookVoteRepository bookVoteRepository) {
        this.bookVoteRepository = bookVoteRepository;
    }

    @Override
    public Object getBookRating(BookEntity bookEntity) {
        var bookRatingFromDb = bookVoteRepository.getBookRating(bookEntity);
        return bookRatingFromDb == null ? false : Math.round(bookRatingFromDb);
    }

    @Override
    public BookRatingInfoDto getBookRatingDto(BookEntity book) {
        var allVotes = bookVoteRepository.findBookVoteEntitiesByBook(book);
        var ratingDto = new BookRatingInfoDto();
        ratingDto.setVoteCount(allVotes.size());
        ratingDto.setRatingValue(((int) Math.round(allVotes.stream().mapToInt(BookVoteEntity::getValue).average().orElse(0))));
        Map<Integer, Integer> starDistributionMap = allVotes
                .stream()
                .collect(
                        Collectors.groupingBy(
                                e -> e.getValue().intValue(),
                                Collectors.reducing(0, e -> 1, Integer::sum)
                        )
                );
        initStarDistributionMapDefaultValues(starDistributionMap);
        ratingDto.setStarDistribution(starDistributionMap);
        return ratingDto;
    }

    @Override
    public void rateBook(UserEntity user, BookEntity book, short rateValue) {
        var vote = bookVoteRepository.findBookVoteEntityByUserAndBook(user, book);
        if (vote == null) {
            vote = new BookVoteEntity();
            vote.setBook(book);
            vote.setUser(user);
            vote.setValue(rateValue);
        } else {
            vote.setValue(rateValue);
        }
        bookVoteRepository.save(vote);
    }

    private void initStarDistributionMapDefaultValues(Map<Integer, Integer> map) {
        for (int i = 1; i <= 5; i++) {
            map.putIfAbsent(i, 0);
        }
    }
}

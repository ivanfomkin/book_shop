package com.example.MyBookShopApp.dto.book;

import lombok.Data;

import java.util.Map;

@Data
public class BookRatingInfoDto {
    private int ratingValue;
    private int voteCount;
    private Map<Integer, Integer> starDistribution;
}

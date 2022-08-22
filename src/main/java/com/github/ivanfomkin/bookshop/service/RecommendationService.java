package com.github.ivanfomkin.bookshop.service;

import org.apache.mahout.cf.taste.common.TasteException;

public interface RecommendationService {
    void getRecommendedBooks() throws TasteException;
}

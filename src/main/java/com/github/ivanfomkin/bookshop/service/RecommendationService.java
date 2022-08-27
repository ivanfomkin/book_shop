package com.github.ivanfomkin.bookshop.service;

import java.util.List;

public interface RecommendationService {
    List<Integer> getRecommendations(int userId, int howMany);
}

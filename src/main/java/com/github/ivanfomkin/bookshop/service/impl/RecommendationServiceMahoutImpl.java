package com.github.ivanfomkin.bookshop.service.impl;

import com.github.ivanfomkin.bookshop.repository.BookRepository;
import com.github.ivanfomkin.bookshop.repository.RecommendationJdbcRepository;
import com.github.ivanfomkin.bookshop.repository.UserRepository;
import com.github.ivanfomkin.bookshop.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationServiceMahoutImpl implements RecommendationService {
    private final Recommender recommender;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final RecommendationJdbcRepository recommendationRepository;

    @Override
    public List<Integer> getRecommendations(int userId, int howMany) {
        return recommendationRepository.getRecommendations(userId, howMany);
    }

    @Scheduled(cron = "${bookshop.recommendations.update.cron}")
    public void calculateUserRecommendations() throws TasteException, SQLException {
        var userIds = userRepository.findAllUserId();
        var booksSize = bookRepository.countAll();
        Map<Integer, List<Integer>> resultMap = new HashMap<>(userIds.size());
        log.info("Запускаю вычисление рекоммендаций для {} пользователей по {} книгам", userIds.size(), booksSize);
        for (Integer userId : userIds) {
            var recommendations = convertRecommenderToItemIdList(recommender.recommend(userId, booksSize));
            resultMap.put(userId, recommendations);
        }
        log.info("Вычисление завершено");
        log.info("Обновляю рекоммендации в БД");
        recommendationRepository.saveRecommendation(resultMap);
        log.info("Обработка рекомендаций завершена");
    }

    private List<Integer> convertRecommenderToItemIdList(List<RecommendedItem> recommendedItems) {
        return recommendedItems.stream().mapToLong(RecommendedItem::getItemID).mapToInt(value -> (int) value).boxed().toList();
    }
}

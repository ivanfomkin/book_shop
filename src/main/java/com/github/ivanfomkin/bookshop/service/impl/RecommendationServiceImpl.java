package com.github.ivanfomkin.bookshop.service.impl;

import com.github.ivanfomkin.bookshop.service.RecommendationService;
import com.github.ivanfomkin.bookshop.service.UserService;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.jdbc.PostgreSQLJDBCDataModel;
import org.apache.mahout.cf.taste.impl.model.jdbc.ReloadFromJDBCDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.GenericItemSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {
    private final UserService userService;
    private final HikariDataSource dataSource;

    @Override
    public void getRecommendedBooks() throws TasteException {
        var user = userService.getCurrentUser();
        if (user != null) {
            DataModel dataModel = new ReloadFromJDBCDataModel(new PostgreSQLJDBCDataModel(dataSource, "book_view_history", "user_id", "book_id", "preference", "last_view_date"));
            UserSimilarity similarity = new EuclideanDistanceSimilarity(dataModel);
            UserNeighborhood neighborhood = new NearestNUserNeighborhood(3, similarity, dataModel);
            Recommender recommender = new GenericUserBasedRecommender(dataModel, neighborhood, similarity);
            var rec = recommender.recommend(user.getId(), 10);
            log.info("Recommendations count: {}", rec.size());
        }
    }
}

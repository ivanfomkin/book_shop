package com.github.ivanfomkin.bookshop.config;

import lombok.RequiredArgsConstructor;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.jdbc.PostgreSQLJDBCDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class MahoutConfig {
    private final DataSource dataSource;

    @Bean
    public DataModel dataModel() {
        return new PostgreSQLJDBCDataModel(dataSource, "book_votes", "user_id", "book_id", "value", null);
    }

    @Bean
    public UserSimilarity userSimilarity() throws TasteException {
        return new PearsonCorrelationSimilarity(dataModel());
    }

    @Bean
    public UserNeighborhood neighborhood() throws TasteException {
        return new NearestNUserNeighborhood(2, userSimilarity(), dataModel());
    }

    @Bean
    public Recommender recommender() throws TasteException {
        return new GenericUserBasedRecommender(dataModel(), neighborhood(), userSimilarity());
    }

}

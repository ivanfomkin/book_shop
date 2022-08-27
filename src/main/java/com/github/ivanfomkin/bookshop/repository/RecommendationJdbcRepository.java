package com.github.ivanfomkin.bookshop.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class RecommendationJdbcRepository {
    private final JdbcTemplate jdbcTemplate;

    public void saveRecommendation(Map<Integer, List<Integer>> calculatedRecommendations) throws SQLException {
        var mapKeys = calculatedRecommendations.keySet().toArray(Integer[]::new);
        var conn = jdbcTemplate.getDataSource().getConnection();
        jdbcTemplate.batchUpdate("""
                INSERT INTO book_recommendations (user_id, recommendations, update_date) VALUES (?, ?, now()) ON CONFLICT (user_id) DO UPDATE SET recommendations = ?, update_date = now()
                """, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Integer userId = mapKeys[i];
                var idArray = calculatedRecommendations.get(userId).toArray(Integer[]::new);
                Array recommendationsArray = conn.createArrayOf("integer", idArray);
                ps.setInt(1, userId);
                ps.setArray(2, recommendationsArray);
                ps.setArray(3, recommendationsArray);
            }

            @Override
            public int getBatchSize() {
                return calculatedRecommendations.size();
            }
        });
    }

    public List<Integer> getRecommendations(int userId, int howMany) {
        try {
            return jdbcTemplate.query("SELECT unnest(recommendations) as book_id FROM book_recommendations WHERE user_id = ? LIMIT ?", (rs, rowNum) -> rs.getInt("book_id"), userId, howMany);
        } catch (EmptyResultDataAccessException e) {
            return List.of();
        }
    }
}

package com.github.ivanfomkin.bookshop.repository;

import com.github.ivanfomkin.bookshop.entity.tag.TagEntity;
import com.github.ivanfomkin.bookshop.entity.tag.TagWithWeightObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<TagEntity, Integer> {
    @Query(value = """
            WITH tag_with_weight AS (SELECT t.name, CAST(COUNT(*) AS REAL) /
            (SELECT COUNT(DISTINCT b2t.book_id) FROM book2tag b2t) AS abnormal_weight FROM book2tag JOIN tags t ON t.id = book2tag.tag_id
            GROUP BY name, t.id),
            normalization_factor AS (SELECT CAST(1 AS REAL) / MAX(t.abnormal_weight) AS max
            FROM tag_with_weight t) SELECT tag.name, ROUND(CAST((tag.abnormal_weight * normalization_factor.max) AS NUMERIC), 2) AS weight FROM
            tag_with_weight tag, normalization_factor
            """, nativeQuery = true)
    List<TagWithWeightObject> findTagsWithWeight();
}

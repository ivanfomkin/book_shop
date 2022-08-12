package com.github.ivanfomkin.bookshop.repository;

import com.github.ivanfomkin.bookshop.entity.genre.GenreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GenreRepository extends JpaRepository<GenreEntity, Integer> {
    Optional<GenreEntity> findGenreEntityBySlug(String slug);
}

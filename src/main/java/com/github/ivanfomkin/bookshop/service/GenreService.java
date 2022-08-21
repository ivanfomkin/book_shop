package com.github.ivanfomkin.bookshop.service;

import com.github.ivanfomkin.bookshop.dto.genre.GenreElementDto;
import com.github.ivanfomkin.bookshop.entity.genre.GenreEntity;

import java.util.List;

public interface GenreService {
    List<GenreEntity> getAllGenres();

    GenreEntity getGenreBySlug(String slug);

    List<GenreElementDto> getAllGenresDto();
}

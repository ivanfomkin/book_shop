package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.entity.genre.GenreEntity;

import java.util.List;

public interface GenreService {
    List<GenreEntity> getAllGenres();

    GenreEntity getGenreBySlug(String slug);
}

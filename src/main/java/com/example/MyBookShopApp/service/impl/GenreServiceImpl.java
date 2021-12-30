package com.example.MyBookShopApp.service.impl;

import com.example.MyBookShopApp.entity.genre.GenreEntity;
import com.example.MyBookShopApp.repository.GenreRepository;
import com.example.MyBookShopApp.service.GenreService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GenreServiceImpl implements GenreService {
    private final GenreRepository genreRepository;

    public GenreServiceImpl(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    @Cacheable(value = "allGenres")
    @Override
    public List<GenreEntity> getAllGenres() {
        return genreRepository.findAll();
    }

    @Cacheable(value = "genreBySlug", key = "#slug")
    @Override
    public GenreEntity getGenreBySlug(String slug) {
        return genreRepository.findGenreEntityBySlug(slug);
    }
}

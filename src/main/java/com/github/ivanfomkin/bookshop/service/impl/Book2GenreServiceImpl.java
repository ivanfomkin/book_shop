package com.github.ivanfomkin.bookshop.service.impl;

import com.github.ivanfomkin.bookshop.entity.book.BookEntity;
import com.github.ivanfomkin.bookshop.entity.book.links.Book2GenreEntity;
import com.github.ivanfomkin.bookshop.entity.genre.GenreEntity;
import com.github.ivanfomkin.bookshop.repository.Book2GenreRepository;
import com.github.ivanfomkin.bookshop.repository.GenreRepository;
import com.github.ivanfomkin.bookshop.service.Book2GenreService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class Book2GenreServiceImpl implements Book2GenreService {
    private final GenreRepository genreRepository;
    private final Book2GenreRepository book2GenreRepository;

    public Book2GenreServiceImpl(GenreRepository genreRepository, Book2GenreRepository book2GenreRepository) {
        this.genreRepository = genreRepository;
        this.book2GenreRepository = book2GenreRepository;
    }

    @Transactional
    @Override
    public void setGenresToBook(BookEntity book, List<String> genreSlugs) {
        var book2Genre = book2GenreRepository.findAllByBook(book);
        var entitiesForDelete = book2Genre.stream().filter(b2g -> !genreSlugs.contains(b2g.getGenre().getSlug())).toList();
        List<Book2GenreEntity> entitiesForSave = new ArrayList<>();
        for (String genreSlug : genreSlugs) {
            var book2TagGenre = book2Genre.stream().filter(b2g -> b2g.getGenre().getSlug().equals(genreSlug)).findFirst().orElse(new Book2GenreEntity());
            book2TagGenre.setBook(book);
            GenreEntity genreEntity = genreRepository.findGenreEntityBySlug(genreSlug).orElseThrow();
            book2TagGenre.setGenre(genreEntity);
            entitiesForSave.add(book2TagGenre);
        }
        book2GenreRepository.saveAll(entitiesForSave);
        book2GenreRepository.deleteAll(entitiesForDelete);
    }
}

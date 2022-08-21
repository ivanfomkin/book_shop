package com.github.ivanfomkin.bookshop.service.impl;

import com.github.ivanfomkin.bookshop.entity.book.BookEntity;
import com.github.ivanfomkin.bookshop.entity.book.links.Book2AuthorEntity;
import com.github.ivanfomkin.bookshop.repository.AuthorRepository;
import com.github.ivanfomkin.bookshop.repository.Book2AuthorRepository;
import com.github.ivanfomkin.bookshop.service.Book2AuthorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class Book2AuthorServiceImpl implements Book2AuthorService {
    private final Book2AuthorRepository book2AuthorRepository;
    private final AuthorRepository authorRepository;

    public Book2AuthorServiceImpl(Book2AuthorRepository book2AuthorRepository, AuthorRepository authorRepository) {
        this.book2AuthorRepository = book2AuthorRepository;
        this.authorRepository = authorRepository;
    }

    @Override
    @Transactional
    public void setAuthorsToBook(BookEntity book, List<String> authorSlugs) {
        var book2Authors = book2AuthorRepository.findAllByBook(book);
        var entitiesForDelete = book2Authors.stream().filter(b2a -> !authorSlugs.contains(b2a.getAuthor().getSlug())).toList();
        List<Book2AuthorEntity> entitiesForSave = new ArrayList<>();
        for (String slug : authorSlugs) {
            var book2AuthorEntity = book2Authors.stream().filter(b2a -> b2a.getAuthor().getSlug().equals(slug)).findFirst().orElse(new Book2AuthorEntity());
            book2AuthorEntity.setBook(book);
            book2AuthorEntity.setAuthor(authorRepository.findAuthorEntityBySlug(slug).orElseThrow());
            book2AuthorEntity.setSortIndex(authorSlugs.indexOf(slug));
            entitiesForSave.add(book2AuthorEntity);
        }
        book2AuthorRepository.saveAll(entitiesForSave);
        book2AuthorRepository.deleteAll(entitiesForDelete);
    }
}

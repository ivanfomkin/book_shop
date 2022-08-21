package com.github.ivanfomkin.bookshop.service.impl;

import com.github.ivanfomkin.bookshop.entity.book.BookEntity;
import com.github.ivanfomkin.bookshop.entity.book.links.Book2TagEntity;
import com.github.ivanfomkin.bookshop.entity.tag.TagEntity;
import com.github.ivanfomkin.bookshop.repository.Book2TagRepository;
import com.github.ivanfomkin.bookshop.repository.TagRepository;
import com.github.ivanfomkin.bookshop.service.Book2TagService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class Book2TagServiceImpl implements Book2TagService {
    private final Book2TagRepository book2TagRepository;
    private final TagRepository tagRepository;

    public Book2TagServiceImpl(Book2TagRepository book2TagRepository, TagRepository tagRepository) {
        this.book2TagRepository = book2TagRepository;
        this.tagRepository = tagRepository;
    }

    @Transactional
    @Override
    public void setTagsToBook(BookEntity book, List<String> tags) {
        var book2Tags = book2TagRepository.findAllByBook(book);
        var entitiesForDelete = book2Tags.stream().filter(b2t -> !tags.contains(b2t.getTag().getName())).toList();
        List<Book2TagEntity> entitiesForSave = new ArrayList<>();
        for (String tag : tags) {
            var book2TagEntity = book2Tags.stream().filter(b2t -> b2t.getTag().getName().equalsIgnoreCase(tag)).findFirst().orElse(new Book2TagEntity());
            book2TagEntity.setBook(book);
            TagEntity tagEntity = tagRepository.findTagEntityByNameEqualsIgnoreCase(tag).orElseThrow();
            book2TagEntity.setTag(tagEntity);
            entitiesForSave.add(book2TagEntity);
        }
        book2TagRepository.saveAll(entitiesForSave);
        book2TagRepository.deleteAll(entitiesForDelete);
    }
}

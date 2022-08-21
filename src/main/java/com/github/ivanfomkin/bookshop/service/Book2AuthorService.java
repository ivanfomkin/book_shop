package com.github.ivanfomkin.bookshop.service;

import com.github.ivanfomkin.bookshop.entity.book.BookEntity;

import java.util.List;

public interface Book2AuthorService {

    void setAuthorsToBook(BookEntity book, List<String> authorSlugs);
}

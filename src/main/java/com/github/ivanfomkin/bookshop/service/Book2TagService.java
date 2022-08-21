package com.github.ivanfomkin.bookshop.service;

import com.github.ivanfomkin.bookshop.entity.book.BookEntity;

import java.util.List;

public interface Book2TagService {
    void setTagsToBook(BookEntity book, List<String> tags);
}

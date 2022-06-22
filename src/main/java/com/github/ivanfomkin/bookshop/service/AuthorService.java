package com.github.ivanfomkin.bookshop.service;

import com.github.ivanfomkin.bookshop.dto.author.AuthorElementDto;
import com.github.ivanfomkin.bookshop.entity.author.AuthorEntity;

import java.util.List;
import java.util.Map;

public interface AuthorService {
    Map<String, List<AuthorEntity>> getAuthorsMap();

    AuthorEntity getAuthorBySlug(String slug);

    List<AuthorElementDto> convertAuthorsToDto(List<AuthorEntity> authors);
}

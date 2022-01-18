package com.example.MyBookShopApp.service;

import com.example.MyBookShopApp.dto.author.AuthorElementDto;
import com.example.MyBookShopApp.entity.author.AuthorEntity;

import java.util.List;
import java.util.Map;

public interface AuthorService {
    Map<String, List<AuthorEntity>> getAuthorsMap();

    AuthorEntity getAuthorBySlug(String slug);

    List<AuthorElementDto> convertAuthorsToDto(List<AuthorEntity> authors);
}
